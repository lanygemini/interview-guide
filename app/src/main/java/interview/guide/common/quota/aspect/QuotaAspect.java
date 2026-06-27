package interview.guide.common.quota.aspect;

import interview.guide.common.auth.CurrentUser;
import interview.guide.common.auth.UserContextHolder;
import interview.guide.common.quota.QuotaExceededException;
import interview.guide.common.quota.QuotaType;
import interview.guide.common.quota.annotation.RequireQuota;
import interview.guide.common.quota.service.QuotaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 额度校验 AOP 切面
 * <p>
 * 拦截 {@link RequireQuota} 注解的方法：
 * 1. 查余额 → 不足抛异常
 * 2. 执行目标方法
 * 3. 成功后扣减额度（后扣策略，避免失败不退的体验问题）
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class QuotaAspect {

    private final QuotaService quotaService;

    @Around("@annotation(requireQuota)")
    public Object checkQuota(ProceedingJoinPoint pjp, RequireQuota requireQuota) throws Throwable {
        QuotaType quotaType = requireQuota.value();
        int amount = requireQuota.amount();

        // 获取当前用户
        CurrentUser currentUser = UserContextHolder.get();
        if (currentUser == null) {
            log.warn("额度校验失败：未获取到当前用户");
            return pjp.proceed(); // 没有用户时放行（匿名场景）
        }

        Long userId = currentUser.id();

        // 1. 查余额
        int remaining = quotaService.getRemaining(userId, quotaType);
        if (remaining < amount) {
            log.warn("额度不足: userId={}, type={}, remaining={}, required={}",
                    userId, quotaType, remaining, amount);
            throw new QuotaExceededException(quotaType);
        }

        // 2. 执行目标方法
        Object result = pjp.proceed();

        // 3. 成功后扣减额度
        try {
            quotaService.consume(userId, quotaType, amount);
        } catch (Exception e) {
            log.error("额度扣减失败: userId={}, type={}, amount={}", userId, quotaType, amount, e);
        }

        return result;
    }
}
