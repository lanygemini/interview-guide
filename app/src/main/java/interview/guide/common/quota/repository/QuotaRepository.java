package interview.guide.common.quota.repository;

import interview.guide.common.quota.model.QuotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 额度仓库
 */
@Repository
public interface QuotaRepository extends JpaRepository<QuotaEntity, Long> {

    Optional<QuotaEntity> findByUserIdAndQuotaType(Long userId, String quotaType);

    /**
     * 乐观锁扣减额度：原子性增加 used_quota，同时检查剩余是否足够
     */
    @Modifying
    @Query("UPDATE QuotaEntity q SET q.usedQuota = q.usedQuota + :amount, q.version = q.version + 1 " +
            "WHERE q.userId = :userId AND q.quotaType = :quotaType " +
            "AND (q.totalQuota - q.usedQuota) >= :amount AND q.version = :version")
    int consumeQuota(@Param("userId") Long userId,
                     @Param("quotaType") String quotaType,
                     @Param("amount") int amount,
                     @Param("version") int version);

    /**
     * 增加额度
     */
    @Modifying
    @Query("UPDATE QuotaEntity q SET q.totalQuota = q.totalQuota + :amount, q.version = q.version + 1 " +
            "WHERE q.userId = :userId AND q.quotaType = :quotaType AND q.version = :version")
    int addQuota(@Param("userId") Long userId,
                 @Param("quotaType") String quotaType,
                 @Param("amount") int amount,
                 @Param("version") int version);
}
