package ziweiyang.toppine.com.oschinadome.ui.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import ziweiyang.toppine.com.oschinadome.R;
import ziweiyang.toppine.com.oschinadome.utils.TLog;

/**
 * 下拉刷新上拉加载控件，目前适用于RecyclerView
 */
public class RecyclerRefreshLayout extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecycleView;

    private int mTouchSlop;

    private SuperRefreshLayoutListener listener;

    private boolean mIsOnLoading = false;

    private boolean mCanLoadMore = true;

    private boolean mHasMore = true;

    private int mYDown;

    private int mLastY;

    public RecyclerRefreshLayout(Context context) {
        this(context, null);
    }

    public RecyclerRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOnRefreshListener(this);
    }


    @Override
    public void onRefresh() {   //刷新
        if (listener != null && !mIsOnLoading) {
            listener.onRefreshing();
        } else
            setRefreshing(false);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView对象
        if (mRecycleView == null) {
            getRecycleView();
        }
    }

    /**
     * 获取RecyclerView，后续支持AbsListView
     */
    private void getRecycleView() {
        if (getChildCount() > 0) {
            View childView = getChildAt(0);
            if (!(childView instanceof RecyclerView)) {//子控件第一个不是RecyclerView,直接通过id找
                childView = findViewById(R.id.recyclerView);
            }
            if (childView != null && childView instanceof RecyclerView) {
                mRecycleView = (RecyclerView) childView;
                mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        TLog.i("----", "滑动状态" + newState);
                        if (newState == RecyclerView.SCROLL_STATE_SETTLING && canLoad() && mCanLoadMore) {
                            //SCROLL_STATE_SETTLING 由于拖动速度快会滑动 SCROLL_STATE_IDLE 不滑动
                            startRequestData();
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (canLoad() && mCanLoadMore) {
                            startRequestData();
                        }
                    }
                });
            }
        }
    }
    boolean falg_StartRequestDate = true;
    public synchronized void startRequestData() {
        if (falg_StartRequestDate) {
            falg_StartRequestDate = false;
            mRecycleView.stopScroll();      //由于滑动太快会出现一个footerView(正在加载中),所以进行一个停止滑动
            loadData();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mYDown = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastY = (int) event.getRawY();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部
     *
     * @return isCanLoad
     */
    private boolean canLoad() {
        return isScrollBottom() && !mIsOnLoading && isPullUp() && mHasMore;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (listener != null) {
            setOnLoading(true);
            TLog.e("滑动到最底部,并可以加载更多:触发加载更多");
            listener.onLoadMore();
        }
    }

    /**
     * 是否是上拉操作
     *
     * @return isPullUp
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * 设置正在加载
     *
     * @param loading 设置正在加载
     */
    public void setOnLoading(boolean loading) {
        mIsOnLoading = loading;
        if (!mIsOnLoading) {
            mYDown = 0;
            mLastY = 0;
        }
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isScrollBottom() {
        return (mRecycleView != null && mRecycleView.getAdapter() != null)
                && getLastVisiblePosition() == (mRecycleView.getAdapter().getItemCount() - 1);
    }

    /**
     * 加载结束记得调用
     */
    public void onComplete() {
        setOnLoading(false);
        setRefreshing(false);
        mHasMore = true;
        falg_StartRequestDate = true;
    }

    /**
     * 是否可加载更多
     *
     * @param mCanLoadMore 是否可加载更多
     */
    public void setCanLoadMore(boolean mCanLoadMore) {
        this.mCanLoadMore = mCanLoadMore;
    }

    /**
     * 获取RecyclerView可见的最后一项
     *
     * @return 可见的最后一项position
     */
    public int getLastVisiblePosition() {
        int position;
        if (mRecycleView.getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) mRecycleView.getLayoutManager()).findLastVisibleItemPosition();
        } else if (mRecycleView.getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) mRecycleView.getLayoutManager()).findLastVisibleItemPosition();
        } else if (mRecycleView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) mRecycleView.getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = mRecycleView.getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions 获得最大的位置
     * @return 获得最大的位置
     */
    private int getMaxPosition(int[] positions) {
        int maxPosition = Integer.MIN_VALUE;
        for (int position : positions) {
            maxPosition = Math.max(maxPosition, position);
        }
        return maxPosition;
    }

    /**
     * 添加加载和刷新
     *
     * @param listener add the listener for SuperRefreshLayout
     */
    public void setSuperRefreshLayoutListener(SuperRefreshLayoutListener listener) {
        this.listener = listener;
    }

    public interface SuperRefreshLayoutListener {
        void onRefreshing();

        void onLoadMore();
    }
}
