/** 抽奖结果 */
export interface DrawResult {
  awardId: string;
  awardTitle: string;
  awardIndex: string;
}

/** 活动账户额度 */
export interface ActivityAccount {
  totalCount: number;
  usedCount: number;
  leftCount: number;
}

/** 积分账户 */
export interface CreditAccount {
  credit: number;
}

/** 签到返利结果 */
export interface SignRebateResult {
  success: boolean;
}

/** SKU 商品 */
export interface SkuProduct {
  sku: string;
  productName: string;
  originalPrice: number;
  deductionPrice: number;
  credit: number;
}

/** 奖品信息 */
export interface Award {
  awardId: number;
  awardTitle: string;
  awardSubtitle: string;
  sort: number;
  awardRuleLockCount: number | null;
  isAwardUnlock: boolean;
  waitUnLockCount: number;
}

/** 活动信息 */
export interface ActivityInfo {
  id: string;
  name: string;
}

/** 用户额度信息 */
export interface QuotaInfo {
  type: string;
  displayName: string;
  remaining: number;
  total: number;
  used: number;
}
