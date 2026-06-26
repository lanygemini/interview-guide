package interview.guide.common.auth.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注入当前登录用户参数
 * <p>
 * 在 Controller 方法参数上使用，自动注入 CurrentUser：
 * <pre>{@code
 * @GetMapping("/profile")
 * public Result<?> profile(@LoginUser CurrentUser user) { ... }
 * }</pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginUser {
}
