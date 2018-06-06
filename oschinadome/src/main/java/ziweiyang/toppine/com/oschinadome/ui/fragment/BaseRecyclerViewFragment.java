package ziweiyang.toppine.com.oschinadome.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ziweiyang.toppine.com.oschinadome.AppConfig;
import ziweiyang.toppine.com.oschinadome.R;
import ziweiyang.toppine.com.oschinadome.adapter.BaseGeneralRecyclerAdapter;
import ziweiyang.toppine.com.oschinadome.adapter.BaseRecyclerAdapter;
import ziweiyang.toppine.com.oschinadome.bean.PageBean;
import ziweiyang.toppine.com.oschinadome.bean.ResultBean;
import ziweiyang.toppine.com.oschinadome.other.AppOperator;
import ziweiyang.toppine.com.oschinadome.ui.view.EmptyLayout;
import ziweiyang.toppine.com.oschinadome.ui.view.RecyclerRefreshLayout;
import ziweiyang.toppine.com.oschinadome.utils.SimplexToast;
import ziweiyang.toppine.com.oschinadome.utils.TDevice;
import ziweiyang.toppine.com.oschinadome.utils.TLog;
import ziweiyang.toppine.com.oschinadome.utils.cache.CacheManager;


/**
 * Fragment里填充了recyclerview,并处理了recyclerview的刷新接口
 * 并用EmptyLayout处理空,错误等状态页面 --- 一般你只要给予
 * getRecyclerAdapter (给个adapter)
 * getType  例如:TypeToken<ResultBean<PageBean<User>>>() {}.getType()
 */
public abstract class BaseRecyclerViewFragment<T> extends BaseFragment implements
        RecyclerRefreshLayout.SuperRefreshLayoutListener,
        BaseRecyclerAdapter.OnItemClickListener,
        View.OnClickListener,
        BaseGeneralRecyclerAdapter.Callback {
    private final String TAG = this.getClass().getSimpleName();
    protected BaseRecyclerAdapter<T> mAdapter;
    protected RecyclerView mRecyclerView;
    protected RecyclerRefreshLayout mRefreshLayout;
    protected boolean isRefreshing;
    protected TextHttpResponseHandler mHandler;
    protected PageBean<T> mBean;
    protected String CACHE_NAME = getClass().getName();
    protected EmptyLayout mErrorLayout;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_base_recycler_view;
    }

    @Override
    protected void initWidget(View root) {
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        mRefreshLayout = (RecyclerRefreshLayout) root.findViewById(R.id.refreshLayout);
        mErrorLayout = (EmptyLayout) root.findViewById(R.id.error_layout);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initData() {
        mBean = new PageBean<>();
        mAdapter = getRecyclerAdapter();    //得到这个Fragment的Adapter   mAdapter=SubFragment 第一次的时候
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, false);   //设置刷新状态 设置不去刷新RecyclerView
        mRecyclerView.setAdapter(mAdapter);     //准备填充这个内容进去  -->奇怪的是Adapter没有执行    onCreateViewHolder
        mAdapter.setOnItemClickListener(this);  //设置item点击事件
        mErrorLayout.setOnLayoutClickListener(this);
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, false);
        mRecyclerView.setLayoutManager(getLayoutManager()); //设置线性布局
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { //滑动监听
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState && getActivity() != null
                        && getActivity().getCurrentFocus() != null) {
                    TDevice.hideSoftKeyboard(getActivity().getCurrentFocus());  //拖拉状态直接隐藏键盘
                }
            }
        });
        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);  //设置刷新的颜色


        mHandler = new TextHttpResponseHandler() {  //用来处理网络请求
            @Override
            public void onStart() {
                super.onStart();
                log("HttpResponseHandler:onStart");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onRequestError();
                log("HttpResponseHandler:onFailure responseString:" + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                log("HttpResponseHandler:onSuccess responseString:" + responseString);
                try {
                    ResultBean<PageBean<T>> resultBean = AppOperator.createGson().fromJson(responseString, getType()); //解析并封装数据
                    if (resultBean != null && resultBean.isSuccess() && resultBean.getResult().getItems() != null) {
                        setListData(resultBean);
                        onRequestSuccess(resultBean.getCode());
                    } else {
                        if (resultBean.getCode() == ResultBean.RESULT_TOKEN_ERROR) {
                            SimplexToast.show(getActivity(), resultBean.getMessage());
                        }   //设置状态
                        mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                onRequestFinish();
                log("HttpResponseHandler:onFinish");
            }

            @Override
            public void onCancel() {
                super.onCancel();
                onRequestFinish();
            }
        };

        boolean isNeedEmptyView = isNeedEmptyView();
        if (isNeedEmptyView) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING); //开始显示页面为   加载中...
            mRefreshLayout.setVisibility(View.GONE);        //设置隐藏正文内容
            mBean = new PageBean<>();

            List<T> items = isNeedCache()   //缓存
                    ? (List<T>) CacheManager.readListJson(getActivity(), CACHE_NAME, getCacheClass())
                    : null;

            mBean.setItems(items);
            //if is the first loading
            if (items == null) {    //没有读取到缓存
                mBean.setItems(new ArrayList<T>());
                onRefreshing(); //刷新
            } else {
                mAdapter.addAll(mBean.getItems());  //添加数据
                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                mRefreshLayout.setVisibility(View.VISIBLE);
                mRoot.post(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(true);
                        onRefreshing();
                    }
                });
            }
        } else {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                    onRefreshing();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        onRefreshing();
    }


    @Override
    public void onItemClick(int position, long itemId) {

    }

    @Override
    public void onRefreshing() {
        isRefreshing = true;
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, true);    //隐藏正文页面,显示刷新页面, 刷新状态不用更新Recycler条目
        requestData();      //在子类中请求数据 ---> 在本mHandle处理请求的数据
    }

    @Override
    public void onLoadMore() {
        mAdapter.setState(isRefreshing ? BaseRecyclerAdapter.STATE_HIDE : BaseRecyclerAdapter.STATE_LOADING, true);
        requestData();
    }

    protected void requestData() {
    }

    protected void onRequestSuccess(int code) {

    }

    protected void onRequestFinish() {
        onComplete();
    }

    protected void onRequestError() {
        onComplete();
        if (mAdapter.getItems().size() == 0) {  //一开始是为0的 --->默认为0,不为0就显示以前的数据
            if (isNeedEmptyView()) mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);    //一直执行,一直返回true
            mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);  //
        }
    }

    protected void onComplete() {
        mRefreshLayout.onComplete();
        isRefreshing = false;
    }

    protected void setListData(ResultBean<PageBean<T>> resultBean) {
        mBean.setNextPageToken(resultBean.getResult().getNextPageToken());
        if (isRefreshing) {     //如果是刷新状态
            AppConfig.getAppConfig(getActivity()).set("system_time", resultBean.getTime()); //把上次刷新的时间记录
            mBean.setItems(resultBean.getResult().getItems());
            mAdapter.clear();
            mAdapter.addAll(mBean.getItems());
            mBean.setPrevPageToken(resultBean.getResult().getPrevPageToken());
            mRefreshLayout.setCanLoadMore(true);
            if (isNeedCache()) {
                CacheManager.saveToJson(getActivity(), CACHE_NAME, mBean.getItems());
            }
        } else {
            mAdapter.addAll(resultBean.getResult().getItems());
        }

        if (resultBean.getResult().getItems() == null
                || resultBean.getResult().getItems().size() < 20)
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
//        mAdapter.setState(resultBean.getResult().getItems() == null
//                || resultBean.getResult().getItems().size() < 20
//                ? BaseRecyclerAdapter.STATE_NO_MORE
//                : BaseRecyclerAdapter.STATE_LOADING, true);

        if (mAdapter.getItems().size() > 0) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mErrorLayout.setErrorType(
                    isNeedEmptyView()
                            ? EmptyLayout.NODATA
                            : EmptyLayout.HIDE_LAYOUT);
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected abstract BaseRecyclerAdapter<T> getRecyclerAdapter();

    protected abstract Type getType();

    /**
     * 获取缓存bean的class
     */
    protected Class<T> getCacheClass() {
        return null;
    }

    @Override
    public Date getSystemTime() {
        return new Date();
    }

    /**
     * 需要缓存
     *
     * @return isNeedCache
     */
    protected boolean isNeedCache() {
        return true;
    }

    /**
     * 需要空的View
     *
     * @return isNeedEmptyView
     */
    protected boolean isNeedEmptyView() {
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    private void log(String msg) {
        if (true)
            TLog.i(TAG, msg);
    }
}
