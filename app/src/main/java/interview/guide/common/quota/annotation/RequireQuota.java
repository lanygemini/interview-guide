package interview.guide.common.quota.annotation;

import interview.guide.common.quota.QuotaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 额度校验注解
 * <p>
 * 标注在需要消耗额度的 Controller 方法上，指定消耗哪种额度类型。
 * 由 {@link interview.guide.common.quota.aspect.QuotaAspect} 拦截处理。
 * <p>
 * 示例：
 * <pre>{@code
 * @RequireQuota(QuotaType.INTERVIEW_COUNT)
 * public Result createSession() { ... }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireQuota {

    /**
     * 需要消耗的额度类型
     */
    QuotaType value();

    /**
     * 消耗数量，默认 1
     */
    int amount() default 1;
}
