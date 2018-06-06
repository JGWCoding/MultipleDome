package ziweiyang.toppine.com.oschinadome.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.OnClick;
import ziweiyang.toppine.com.oschinadome.R;
import ziweiyang.toppine.com.oschinadome.Setting;
import ziweiyang.toppine.com.oschinadome.bean.SubTab;
import ziweiyang.toppine.com.oschinadome.interfaces.OnTabReselectListener;
import ziweiyang.toppine.com.oschinadome.utils.AccountHelper;

/**
 * 发现 页面
 */

public class ExploreFragment extends BaseTitleFragment implements View.OnClickListener, OnTabReselectListener {

    @Bind(R.id.rl_soft)
    View mRlActive;

    @Bind(R.id.rl_scan)
    View mScan;

    @Bind(R.id.iv_has_location)
    ImageView mIvLocated;

    @Override
    protected int getIconRes() {
        return R.mipmap.btn_search_normal;
    }

    @Override
    protected View.OnClickListener getIconClickListener() {     //search Activity
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SearchActivity.show(getContext());//todo
            }
        };
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_explore;
    }

    @Override
    protected int getTitleRes() {
        return R.string.main_tab_name_explore;
    }

    @Override
    public void onResume() {
        super.onResume();
        hasLocation();      //显示 附近的程序员 是否有你的位置信息
    }

    @OnClick({R.id.rl_git, R.id.rl_gits,
            R.id.rl_soft, R.id.rl_scan,
            R.id.rl_shake, R.id.layout_events,
            R.id.layout_nearby})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_git:   //码云推荐
//                FeatureActivity.show(getActivity());//TODO
                break;
            case R.id.rl_gits:  //代码片段
//                GistActivity.show(mContext);//TODO
                break;
            case R.id.rl_soft: //开源软件
//                UIHelper.showSimpleBack(getActivity(),
//                        SimpleBackPage.OPEN_SOURCE_SOFTWARE);//TODO
                break;
            case R.id.rl_scan: //扫一扫
//                UIHelper.showScanActivity(getActivity());//TODO
                break;
            case R.id.rl_shake: //摇一摇
                showShake();
                break;
            case R.id.layout_events: //线下活动
                SubTab tab = new SubTab();

                SubTab.Banner banner = tab.new Banner();
                banner.setCatalog(3);
                banner.setHref("https://www.oschina.net/action/apiv2/banner?catalog=3");
                tab.setBanner(banner);

                tab.setName("线下活动");
                tab.setFixed(false);
                tab.setHref("https://www.oschina.net/action/apiv2/sub_list?token=727d77c15b2ca641fff392b779658512");
                tab.setNeedLogin(false);
                tab.setSubtype(1);
                tab.setOrder(74);
                tab.setToken("727d77c15b2ca641fff392b779658512");
                tab.setType(5);

                Bundle bundle = new Bundle();
                bundle.putSerializable("sub_tab", tab);

//                UIHelper.showSimpleBack(getContext(), SimpleBackPage.OUTLINE_EVENTS, bundle);//TODO
                break;
            case R.id.layout_nearby:    //附近的程序员
                if (!AccountHelper.isLogin()) {
//                    LoginActivity.show(getContext());//TODO
                    break;
                }
//                NearbyActivity.show(getContext());//TODO
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabReselect() {
        hasLocation();
    }

    private void showShake() {
//        ShakePresentActivity.show(getActivity()); //TODO
    }

    private void hasLocation() {
        boolean hasLocation = Setting.hasLocation(getContext());
        if (hasLocation) {
            mIvLocated.setVisibility(View.VISIBLE);
        } else {
            mIvLocated.setVisibility(View.GONE);
        }
    }
}
