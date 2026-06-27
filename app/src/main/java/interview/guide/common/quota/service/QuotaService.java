package interview.guide.common.quota.service;

import interview.guide.common.quota.QuotaExceededException;
import interview.guide.common.quota.QuotaType;
import interview.guide.common.quota.model.QuotaDTO;
import interview.guide.common.quota.model.QuotaEntity;
import interview.guide.common.quota.repository.QuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 额度服务
 * <p>
 * 提供额度查询、扣减、增加操作。扣减使用乐观锁防并发超卖。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuotaService {

    private final QuotaRepository quotaRepository;

    /**
     * 查询剩余额度
     */
    public int getRemaining(Long userId, QuotaType quotaType) {
        return quotaRepository.findByUserIdAndQuotaType(userId, quotaType.getCode())
                .map(QuotaEntity::getRemaining)
                .orElse(0);
    }

    /**
     * 扣减额度
     */
    @Transactional
    public void consume(Long userId, QuotaType quotaType, int amount) {
        QuotaEntity entity = quotaRepository.findByUserIdAndQuotaType(userId, quotaType.getCode())
                .orElseThrow(() -> new QuotaExceededException(quotaType));

        if (entity.getRemaining() < amount) {
            throw new QuotaExceededException(quotaType);
        }

        int affected = quotaRepository.consumeQuota(userId, quotaType.getCode(), amount, entity.getVersion());
        if (affected == 0) {
            // 乐观锁冲突或额度不足（并发场景）
            throw new QuotaExceededException(quotaType);
        }
        log.debug("额度扣减: userId={}, type={}, amount={}", userId, quotaType, amount);
    }

    /**
     * 增加额度（幂等：调用方保证 bizId 不重复）
     */
    @Transactional
    public void addQuota(Long userId, QuotaType quotaType, int amount) {
        QuotaEntity entity = quotaRepository.findByUserIdAndQuotaType(userId, quotaType.getCode())
                .orElse(null);

        if (entity == null) {
            // 首次分配，新建记录
            entity = QuotaEntity.builder()
                    .userId(userId)
                    .quotaType(quotaType.getCode())
                    .totalQuota(amount)
                    .usedQuota(0)
                    .build();
            quotaRepository.save(entity);
            log.info("额度初始化: userId={}, type={}, amount={}", userId, quotaType, amount);
        } else {
            int affected = quotaRepository.addQuota(userId, quotaType.getCode(), amount, entity.getVersion());
            if (affected == 0) {
                // 乐观锁冲突重试
                log.warn("增加额度乐观锁冲突，重试: userId={}, type={}", userId, quotaType);
                addQuota(userId, quotaType, amount);
                return;
            }
            log.info("额度增加: userId={}, type={}, amount={}", userId, quotaType, amount);
        }
    }

    /**
     * 初始化默认额度（新用户注册时调用）
     */
    @Transactional
    public void initDefaultQuota(Long userId) {
        // 先给面试次数
        addQuota(userId, QuotaType.INTERVIEW_COUNT, 3);
        // 文档分析次数
        addQuota(userId, QuotaType.DOCUMENT_ANALYZE, 5);
    }

    /**
     * 获取用户所有额度
     */
    public List<QuotaDTO> getAllQuotas(Long userId) {
        List<QuotaDTO> quotas = new ArrayList<>();
        for (QuotaType type : QuotaType.values()) {
            QuotaEntity entity = quotaRepository.findByUserIdAndQuotaType(userId, type.getCode())
                    .orElse(null);
            if (entity != null) {
                quotas.add(QuotaDTO.builder()
                        .type(type.getCode())
                        .displayName(type.getDisplayName())
                        .remaining(entity.getRemaining())
                        .total(entity.getTotalQuota())
                        .used(entity.getUsedQuota())
                        .build());
            } else {
                quotas.add(QuotaDTO.builder()
                        .type(type.getCode())
                        .displayName(type.getDisplayName())
                        .remaining(0)
                        .total(0)
                        .used(0)
                        .build());
            }
        }
        return quotas;
    }
}
