package interview.guide.common.auth;

import interview.guide.common.auth.annotation.RequireLogin;
import interview.guide.common.exception.BusinessException;
import interview.guide.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录校验拦截器
 * <p>
 * 检查目标方法或类是否标注了 {@link RequireLogin}，
 * 若标注且当前无登录态，则抛出 BusinessException(UNAUTHORIZED)。
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 检查方法或类级别是否有 @RequireLogin
        RequireLogin requireLogin = handlerMethod.getMethodAnnotation(RequireLogin.class);
        if (requireLogin == null) {
            requireLogin = handlerMethod.getBeanType().getAnnotation(RequireLogin.class);
        }
        if (requireLogin == null) {
            return true;
        }

        // 检查当前是否有登录态
        CurrentUser currentUser = UserContextHolder.get();
        if (currentUser == null) {
            log.warn("未授权访问: {}", request.getRequestURI());
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return true;
    }
}
