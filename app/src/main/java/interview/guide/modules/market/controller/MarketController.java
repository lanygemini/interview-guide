package interview.guide.modules.market.controller;

import interview.guide.common.auth.CurrentUser;
import interview.guide.common.auth.annotation.LoginUser;
import interview.guide.common.auth.annotation.RequireLogin;
import interview.guide.common.result.Result;
import interview.guide.modules.market.model.ActivityAccountDTO;
import interview.guide.modules.market.model.ActivityInfoDTO;
import interview.guide.modules.market.model.AwardDTO;
import interview.guide.modules.market.model.CreditAccountDTO;
import interview.guide.modules.market.model.DrawResultDTO;
import interview.guide.modules.market.model.SignRebateResultDTO;
import interview.guide.modules.market.model.SkuProductDTO;
import interview.guide.modules.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 营销控制器
 * <p>
 * 所有接口需要登录。
 */
@Slf4j
@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
@RequireLogin
public class MarketController {

    private final MarketService marketService;

    @GetMapping("/activities")
    public Result<List<ActivityInfoDTO>> getActivities() {
        List<ActivityInfoDTO> activities = marketService.getActivities();
        return Result.success(activities);
    }

    @PostMapping("/draw")
    public Result<DrawResultDTO> draw(@LoginUser CurrentUser user,
                                      @RequestParam(required = false) String activityId) {
        String userId = String.valueOf(user.id());
        log.info("用户抽奖: userId={}, activityId={}", userId, activityId);
        DrawResultDTO result = marketService.draw(userId, activityId);
        return Result.success(result);
    }

    @GetMapping("/account")
    public Result<ActivityAccountDTO> getAccount(@LoginUser CurrentUser user,
                                                 @RequestParam(required = false) String activityId) {
        String userId = String.valueOf(user.id());
        ActivityAccountDTO account = marketService.getAccount(userId, activityId);
        return Result.success(account);
    }

    @GetMapping("/credit")
    public Result<CreditAccountDTO> getCredit(@LoginUser CurrentUser user) {
        String userId = String.valueOf(user.id());
        CreditAccountDTO credit = marketService.getCredit(userId);
        return Result.success(credit);
    }

    @GetMapping("/awards")
    public Result<List<AwardDTO>> getAwardList(@LoginUser CurrentUser user,
                                               @RequestParam(required = false) String activityId) {
        String userId = String.valueOf(user.id());
        List<AwardDTO> awards = marketService.getAwardList(userId, activityId);
        return Result.success(awards);
    }

    @PostMapping("/sign")
    public Result<SignRebateResultDTO> sign(@LoginUser CurrentUser user) {
        String userId = String.valueOf(user.id());
        log.info("用户签到: userId={}", userId);
        SignRebateResultDTO result = marketService.sign(userId);
        return Result.success(result);
    }

    @GetMapping("/sign/status")
    public Result<SignRebateResultDTO> getSignStatus(@LoginUser CurrentUser user) {
        String userId = String.valueOf(user.id());
        SignRebateResultDTO result = marketService.getSignStatus(userId);
        return Result.success(result);
    }

    @GetMapping("/skus")
    public Result<List<SkuProductDTO>> getSkus(@LoginUser CurrentUser user,
                                               @RequestParam(required = false) String activityId) {
        List<SkuProductDTO> skus = marketService.getSkus(activityId);
        return Result.success(skus);
    }
}
