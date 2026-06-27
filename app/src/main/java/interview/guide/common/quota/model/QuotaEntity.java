package interview.guide.common.quota.model;

import interview.guide.common.quota.QuotaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户额度实体
 * <p>
 * 一行一种额度类型，通过 (user_id, quota_type) 唯一索引定位。
 * version 字段用于乐观锁防并发超扣。
 */
@Entity
@Table(name = "user_quota", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_quota_type", columnNames = {"user_id", "quota_type"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "quota_type", nullable = false, length = 32)
    private String quotaType;

    @Column(name = "total_quota", nullable = false)
    @Builder.Default
    private Integer totalQuota = 0;

    @Column(name = "used_quota", nullable = false)
    @Builder.Default
    private Integer usedQuota = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer version = 0;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    /** 剩余额度 */
    public int getRemaining() {
        return totalQuota - usedQuota;
    }
}
