package interview.guide.common.auth;

/**
 * 当前登录用户
 *
 * @param id       用户ID（数据库自增主键）
 * @param username 用户名
 */
public record CurrentUser(Long id, String username) {
}
