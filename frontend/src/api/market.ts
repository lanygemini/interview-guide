import request from './request';
import type { ActivityAccount, ActivityInfo, Award, CreditAccount, DrawResult, SignRebateResult, SkuProduct } from '../types/market';

export const marketApi = {
  /** 获取可选活动列表 */
  getActivities(): Promise<ActivityInfo[]> {
    return request.get<ActivityInfo[]>('/api/market/activities');
  },

  /** 抽奖 */
  draw(activityId?: string): Promise<DrawResult> {
    const params = activityId ? { activityId } : undefined;
    return request.post<DrawResult>('/api/market/draw', undefined, { params });
  },

  /** 查询活动账户额度 */
  getAccount(activityId?: string): Promise<ActivityAccount> {
    const params = activityId ? { activityId } : undefined;
    return request.get<ActivityAccount>('/api/market/account', { params });
  },

  /** 查询积分 */
  getCredit(): Promise<CreditAccount> {
    return request.get<CreditAccount>('/api/market/credit');
  },

  /** 签到返利 */
  sign(): Promise<SignRebateResult> {
    return request.post<SignRebateResult>('/api/market/sign');
  },

  /** 查询签到状态 */
  getSignStatus(): Promise<SignRebateResult> {
    return request.get<SignRebateResult>('/api/market/sign/status');
  },

  /** 查询奖品列表 */
  getAwards(activityId?: string): Promise<Award[]> {
    const params = activityId ? { activityId } : undefined;
    return request.get<Award[]>('/api/market/awards', { params });
  },

  /** 查询 SKU 商品列表 */
  getSkus(activityId?: string): Promise<SkuProduct[]> {
    const params = activityId ? { activityId } : undefined;
    return request.get<SkuProduct[]>('/api/market/skus', { params });
  },
};
