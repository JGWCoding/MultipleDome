package ziweiyang.toppine.com.oschinadome.ui.view;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.RequestManager;

import ziweiyang.toppine.com.oschinadome.R;
import ziweiyang.toppine.com.oschinadome.bean.Banner;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class EventHeaderView extends HeaderView {
    public EventHeaderView(Context context, RequestManager loader, String api, String bannerCache) {
        super(context, loader, api, bannerCache);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_event_banner;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        //mBannerView.setTransformer(new ScaleTransform());
    }

    @Override
    public void onItemClick(int position) {
        Banner banner = mBanners.get(position);
//        if (banner != null)   //todo 注释掉了
//            EventDetailActivity.show(getContext(), banner.getId());
    }

    @Override
    protected View instantiateItem(int position) {
        ViewEventBanner view = new ViewEventBanner(getContext());
        if (mBanners.size() != 0) {
            int p = position % mBanners.size();
            if (p >= 0 && p < mBanners.size()) {
                view.initData(mImageLoader, mBanners.get(p));
            }
        }
        return view;
    }
}
