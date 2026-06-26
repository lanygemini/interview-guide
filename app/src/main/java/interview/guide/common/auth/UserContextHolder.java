package interview.guide.common.auth;

/**
 * 用户上下文持有者（基于 ThreadLocal）
 * <p>
 * 每个请求线程持有当前登录用户信息，请求结束后必须 clear。
 */
public final class UserContextHolder {

    private static final ThreadLocal<CurrentUser> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void set(CurrentUser user) {
        CONTEXT.set(user);
    }

    public static CurrentUser get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
