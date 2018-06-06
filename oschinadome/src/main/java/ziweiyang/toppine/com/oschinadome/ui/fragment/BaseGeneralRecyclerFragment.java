package ziweiyang.toppine.com.oschinadome.ui.fragment;

import ziweiyang.toppine.com.oschinadome.interfaces.OnTabReselectListener;

/**
 * Fragment里填充了RecyclerView,并用EmptyLayout处理空,错误等页面,还有控制刷新页面,并处理的tab切换的基本处理
 * on 2016/8/30.
 */
public abstract class BaseGeneralRecyclerFragment<T> extends BaseRecyclerViewFragment<T> implements OnTabReselectListener {
    @Override
    public void onTabReselect() {
        if (mRecyclerView != null && !isRefreshing) {
            mRecyclerView.scrollToPosition(0);
            mRefreshLayout.setRefreshing(true);
            onRefreshing();
        }
    }
}
