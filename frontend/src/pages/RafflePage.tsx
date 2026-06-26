import { useState, useEffect, useCallback } from 'react';
import { Gift, RefreshCw, Trophy, Coins, CalendarDays, Loader2, Sparkles, Lock, Unlock, ChevronDown } from 'lucide-react';
import { marketApi } from '../api/market';
import { getErrorMessage } from '../api/request';
import type { ActivityAccount, ActivityInfo, Award, CreditAccount, DrawResult } from '../types/market';

export default function RafflePage() {
  const [activities, setActivities] = useState<ActivityInfo[]>([]);
  const [selectedActivityId, setSelectedActivityId] = useState<string>('');
  const [account, setAccount] = useState<ActivityAccount | null>(null);
  const [credit, setCredit] = useState<CreditAccount | null>(null);
  const [drawResult, setDrawResult] = useState<DrawResult | null>(null);
  const [signStatus, setSignStatus] = useState(false);
  const [awards, setAwards] = useState<Award[]>([]);
  const [drawing, setDrawing] = useState(false);
  const [signing, setSigning] = useState(false);
  const [error, setError] = useState('');
  const [activitiesLoading, setActivitiesLoading] = useState(true);

  // 加载活动列表
  useEffect(() => {
    marketApi.getActivities()
      .then((list) => {
        setActivities(list);
        if (list.length > 0) {
          setSelectedActivityId(list[0].id);
        }
        setActivitiesLoading(false);
      })
      .catch((err) => {
        setError(getErrorMessage(err));
        setActivitiesLoading(false);
      });
  }, []);

  const loadData = useCallback(async () => {
    if (!selectedActivityId) return;
    try {
      const [accountData, creditData, signStatusData, awardData] = await Promise.all([
        marketApi.getAccount(selectedActivityId),
        marketApi.getCredit(),
        marketApi.getSignStatus(),
        marketApi.getAwards(selectedActivityId),
      ]);
      setAccount(accountData);
      setCredit(creditData);
      setSignStatus(signStatusData.success);
      setAwards(awardData);
      setError('');
    } catch (err) {
      setError(getErrorMessage(err));
    }
  }, [selectedActivityId]);

  // 活动切换时重新加载数据
  useEffect(() => {
    if (selectedActivityId) {
      setDrawResult(null);
      loadData();
    }
  }, [selectedActivityId, loadData]);

  const handleDraw = async () => {
    setDrawing(true);
    setError('');
    setDrawResult(null);
    try {
      const result = await marketApi.draw(selectedActivityId);
      setDrawResult(result);
      loadData();
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setDrawing(false);
    }
  };

  const handleSign = async () => {
    setSigning(true);
    setError('');
    try {
      await marketApi.sign();
      setSignStatus(true);
      loadData();
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSigning(false);
    }
  };

  if (activitiesLoading) {
    return (
      <div className="flex items-center justify-center min-h-[50vh]">
        <Loader2 className="w-8 h-8 text-primary-500 animate-spin" />
      </div>
    );
  }

  const currentActivity = activities.find(a => a.id === selectedActivityId);

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* 标题 + 活动选择器 */}
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-white flex items-center gap-2">
            <Gift className="w-6 h-6 text-primary-500" />
            每日抽奖
          </h1>
          <p className="text-slate-500 dark:text-slate-400 mt-1">签到获取积分，参与抽奖赢好礼</p>
        </div>
        <div className="flex items-center gap-3">
          {/* 活动选择器 */}
          {activities.length > 1 && (
            <div className="relative">
              <select
                value={selectedActivityId}
                onChange={(e) => setSelectedActivityId(e.target.value)}
                className="appearance-none px-4 py-2 pr-10 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-white text-sm focus:ring-2 focus:ring-primary-500 focus:border-transparent outline-none transition-all cursor-pointer"
              >
                {activities.map((a) => (
                  <option key={a.id} value={a.id}>{a.name}</option>
                ))}
              </select>
              <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
            </div>
          )}
          <button
            onClick={loadData}
            className="p-2 text-slate-400 hover:text-slate-600 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors"
            title="刷新"
          >
            <RefreshCw className="w-5 h-5" />
          </button>
        </div>
      </div>

      {error && (
        <div className="p-3 bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 rounded-xl text-red-600 dark:text-red-400 text-sm">
          {error}
        </div>
      )}

      {/* 当前活动名称 */}
      {currentActivity && (
        <div className="px-4 py-2 bg-primary-50 dark:bg-primary-900/20 rounded-xl text-sm text-primary-600 dark:text-primary-400 font-medium">
          当前活动：{currentActivity.name}
        </div>
      )}

      {/* 数据卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* 抽奖额度 */}
        <div className="bg-white dark:bg-slate-900 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-5">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-10 h-10 bg-amber-100 dark:bg-amber-900/30 rounded-xl flex items-center justify-center">
              <Trophy className="w-5 h-5 text-amber-600 dark:text-amber-400" />
            </div>
            <div>
              <p className="text-sm text-slate-500 dark:text-slate-400">抽奖额度</p>
              <p className="text-2xl font-bold text-slate-900 dark:text-white">
                {account ? account.leftCount : '-'}
                <span className="text-sm font-normal text-slate-400 ml-1">次</span>
              </p>
            </div>
          </div>
          {account && (
            <p className="text-xs text-slate-400">
              已用 {account.usedCount} / 总计 {account.totalCount}
            </p>
          )}
        </div>

        {/* 积分 */}
        <div className="bg-white dark:bg-slate-900 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-5">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-10 h-10 bg-emerald-100 dark:bg-emerald-900/30 rounded-xl flex items-center justify-center">
              <Coins className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
            </div>
            <div>
              <p className="text-sm text-slate-500 dark:text-slate-400">我的积分</p>
              <p className="text-2xl font-bold text-slate-900 dark:text-white">
                {credit ? credit.credit : '-'}
              </p>
            </div>
          </div>
        </div>

        {/* 签到 */}
        <div className="bg-white dark:bg-slate-900 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-5">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-10 h-10 bg-blue-100 dark:bg-blue-900/30 rounded-xl flex items-center justify-center">
              <CalendarDays className="w-5 h-5 text-blue-600 dark:text-blue-400" />
            </div>
            <div>
              <p className="text-sm text-slate-500 dark:text-slate-400">今日签到</p>
              {signStatus ? (
                <p className="text-lg font-bold text-green-600 dark:text-green-400">已签到</p>
              ) : (
                <button
                  onClick={handleSign}
                  disabled={signing}
                  className="text-sm px-4 py-1 bg-gradient-to-r from-primary-500 to-primary-600 text-white rounded-lg hover:from-primary-600 hover:to-primary-700 disabled:opacity-50 transition-all"
                >
                  {signing ? '签到中...' : '去签到'}
                </button>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* 抽奖区域 */}
      <div className="bg-white dark:bg-slate-900 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-8 text-center">
        {drawResult ? (
          <div className="space-y-4">
            <div className="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-amber-400 to-amber-600 rounded-full shadow-lg mb-2">
              <Trophy className="w-10 h-10 text-white" />
            </div>
            <h2 className="text-2xl font-bold text-slate-900 dark:text-white">
              恭喜获得！
            </h2>
            <div className="inline-block px-6 py-3 bg-gradient-to-r from-amber-50 to-amber-100 dark:from-amber-900/30 dark:to-amber-800/30 rounded-xl">
              <p className="text-xl font-bold text-amber-600 dark:text-amber-400">
                {drawResult.awardTitle}
              </p>
            </div>
            <button
              onClick={() => setDrawResult(null)}
              className="text-sm text-primary-500 hover:text-primary-600 font-medium"
            >
              {account && account.leftCount > 0 ? '再抽一次' : '知道了'}
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            <div className="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-primary-400 to-primary-600 rounded-full shadow-lg mb-2">
              <Sparkles className="w-10 h-10 text-white" />
            </div>
            <h2 className="text-xl font-bold text-slate-900 dark:text-white">
              点击抽奖
            </h2>
            <p className="text-slate-500 dark:text-slate-400 text-sm">
              {account && account.leftCount > 0
                ? `你还有 ${account.leftCount} 次抽奖机会`
                : '暂无抽奖额度，请稍后再来'}
            </p>
            <button
              onClick={handleDraw}
              disabled={drawing || !account || account.leftCount <= 0}
              className="px-8 py-3 bg-gradient-to-r from-primary-500 to-primary-600 text-white rounded-xl font-medium hover:from-primary-600 hover:to-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all inline-flex items-center gap-2 shadow-lg shadow-primary-500/30"
            >
              {drawing ? (
                <Loader2 className="w-5 h-5 animate-spin" />
              ) : (
                <Gift className="w-5 h-5" />
              )}
              {drawing ? '抽奖中...' : '开始抽奖'}
            </button>
          </div>
        )}
      </div>

      {/* 奖品列表 */}
      {awards.length > 0 && (
        <div className="bg-white dark:bg-slate-900 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-6">
          <h2 className="text-lg font-bold text-slate-900 dark:text-white mb-4 flex items-center gap-2">
            <Trophy className="w-5 h-5 text-amber-500" />
            奖品列表
            {currentActivity && (
              <span className="text-sm font-normal text-slate-400">（{currentActivity.name}）</span>
            )}
          </h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
            {awards.map((award) => (
              <div
                key={award.awardId}
                className={`p-4 rounded-xl border transition-all ${
                  award.isAwardUnlock
                    ? 'bg-gradient-to-br from-amber-50 to-white dark:from-amber-900/20 dark:to-slate-800 border-amber-200 dark:border-amber-800'
                    : 'bg-slate-50 dark:bg-slate-800/50 border-slate-200 dark:border-slate-700 opacity-70'
                }`}
              >
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm font-bold text-slate-900 dark:text-white">
                    {award.awardTitle}
                  </span>
                  {award.isAwardUnlock ? (
                    <Unlock className="w-4 h-4 text-green-500" />
                  ) : (
                    <Lock className="w-4 h-4 text-slate-400" />
                  )}
                </div>
                {award.awardSubtitle && (
                  <p className="text-xs text-slate-500 dark:text-slate-400">
                    {award.awardSubtitle}
                  </p>
                )}
                {!award.isAwardUnlock && award.waitUnLockCount > 0 && (
                  <p className="text-xs text-amber-600 dark:text-amber-400 mt-1">
                    再抽 {award.waitUnLockCount} 次解锁
                  </p>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
