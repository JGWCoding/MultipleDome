package ziweiyang.toppine.com.oschinadome.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import ziweiyang.toppine.com.oschinadome.AppConfig;
import ziweiyang.toppine.com.oschinadome.OSCApplication;
import ziweiyang.toppine.com.oschinadome.adapter.BaseRecyclerAdapter;
import ziweiyang.toppine.com.oschinadome.adapter.NewsSubAdapter;
import ziweiyang.toppine.com.oschinadome.bean.PageBean;
import ziweiyang.toppine.com.oschinadome.bean.ResultBean;
import ziweiyang.toppine.com.oschinadome.bean.SubBean;
import ziweiyang.toppine.com.oschinadome.bean.SubTab;
import ziweiyang.toppine.com.oschinadome.net.OSChinaApi;
import ziweiyang.toppine.com.oschinadome.ui.view.EventHeaderView;
import ziweiyang.toppine.com.oschinadome.ui.view.HeaderView;
import ziweiyang.toppine.com.oschinadome.ui.view.NewsHeaderView;
import ziweiyang.toppine.com.oschinadome.utils.TDevice;

/**
 * 每日乱弹 界面
 *
 */

public class SubFragment extends BaseGeneralRecyclerFragment<SubBean> {
    private SubTab mTab;
    private HeaderView mHeaderView;
    private OSCApplication.ReadState mReadState;

    public static SubFragment newInstance(Context context, SubTab subTab) {
        SubFragment fragment = new SubFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_tab", subTab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mTab = (SubTab) bundle.getSerializable("sub_tab");
        CACHE_NAME = mTab.getToken();
    }

    @Override
    public void initData() {
        mReadState = OSCApplication.getReadState("sub_list");
        if (mTab.getBanner() != null) {
            mHeaderView = mTab.getBanner().getCatalog() == SubTab.BANNER_CATEGORY_NEWS ?
                    new NewsHeaderView(mContext, getImgLoader(), mTab.getBanner().getHref(), mTab.getToken() + "banner" + mTab.getType()) :
                    new EventHeaderView(mContext, getImgLoader(), mTab.getBanner().getHref(), mTab.getToken() + "banner" + mTab.getType());
        }
        super.initData();
        mAdapter.setHeaderView(mHeaderView);
        mAdapter.setSystemTime(AppConfig.getAppConfig(getActivity()).get("system_time"));
        if (mAdapter instanceof NewsSubAdapter) {
            ((NewsSubAdapter) mAdapter).setTab(mTab);
        }
    }

    @Override
    public void onItemClick(int position, long itemId) {    //item点击跳转详情Activity
        if(!TDevice.hasWebView(mContext))
            return;
        SubBean sub = mAdapter.getItem(position);
        if (sub == null)
            return;
//        switch (sub.getType()) {  //TODO 注释掉了
//            case News.TYPE_SOFTWARE:
//                //SoftwareDetailActivity.show(mContext, sub.getId());
//                SoftwareDetailActivity.show(mContext, sub);
//                break;
//            case News.TYPE_QUESTION:
//                //QuestionDetailActivity.show(mContext, sub.getId());
//                QuestionDetailActivity.show(mContext, sub);
//                break;
//            case News.TYPE_BLOG:
//                //BlogDetailActivity.show(mContext, sub.getId());
//                BlogDetailActivity.show(mContext, sub);
//                break;
//            case News.TYPE_TRANSLATE:
//                //TranslateDetailActivity.show(mContext, sub.getId());
//                NewsDetailActivity.show(mContext, sub);
//                break;
//            case News.TYPE_EVENT:
//                //EventDetailActivity.show(mContext, sub.getId());
//                EventDetailActivity.show(mContext, sub);
//                break;
//            case News.TYPE_NEWS:
//                //NewsDetailActivity.show(mContext, sub.getId());
//                NewsDetailActivity.show(mContext, sub);
//                break;
//            default:
//                UIHelper.showUrlRedirect(mContext, sub.getHref());
//                break;
//        }

        mReadState.put(sub.getKey());
        mAdapter.updateItem(position);
    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
        if (mHeaderView != null)
            mHeaderView.requestBanner();
    }

    @Override   //请求数据
    protected void requestData() {
        OSChinaApi.getSubscription(mTab.getHref(), isRefreshing ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected void setListData(ResultBean<PageBean<SubBean>> resultBean) {
        super.setListData(resultBean);
        mAdapter.setSystemTime(resultBean.getTime());
    }

    @Override
    protected BaseRecyclerAdapter<SubBean> getRecyclerAdapter() {
        int mode = mHeaderView != null ? BaseRecyclerAdapter.BOTH_HEADER_FOOTER : BaseRecyclerAdapter.ONLY_FOOTER;//ONLY_FOOTER
//        if (mTab.getType() == News.TYPE_BLOG) // TODO 注释掉了
//            return new BlogSubAdapter(getActivity(), mode);
//        else if (mTab.getType() == News.TYPE_EVENT)
//            return new EventSubAdapter(this, mode);
//        else if (mTab.getType() == News.TYPE_QUESTION)
//            return new QuestionSubAdapter(this, mode);
        //第一次返回这个 返回这个Adapter mode=3 BOTH_HEADER_FOOTER 这里面初始化了mState = STATE_HIDE;为加载中状态
        return new NewsSubAdapter(getActivity(), mode);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<SubBean>>>() {
        }.getType();
    }

    @Override
    protected Class<SubBean> getCacheClass() {
        return SubBean.class;
    }
}
