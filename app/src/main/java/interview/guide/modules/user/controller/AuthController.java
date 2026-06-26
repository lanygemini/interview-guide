package interview.guide.modules.user.controller;

import interview.guide.common.auth.CurrentUser;
import interview.guide.common.auth.JwtService;
import interview.guide.common.auth.annotation.LoginUser;
import interview.guide.common.auth.annotation.RequireLogin;
import interview.guide.common.result.Result;
import interview.guide.modules.user.model.LoginRequest;
import interview.guide.modules.user.model.LoginResponse;
import interview.guide.modules.user.model.RegisterRequest;
import interview.guide.modules.user.model.UserDTO;
import interview.guide.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public Result<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册: username={}", request.getUsername());
        UserDTO user = userService.register(request);
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录: username={}", request.getUsername());
        UserDTO user = userService.authenticate(request);
        String token = jwtService.generateToken(user.getId(), user.getUsername());
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .user(user)
                .build();
        return Result.success(response);
    }

    @GetMapping("/me")
    @RequireLogin
    public Result<UserDTO> me(@LoginUser CurrentUser currentUser) {
        UserDTO user = userService.getById(currentUser.id());
        return Result.success(user);
    }
}
