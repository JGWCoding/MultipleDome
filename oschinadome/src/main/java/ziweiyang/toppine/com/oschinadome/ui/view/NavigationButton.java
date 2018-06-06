package ziweiyang.toppine.com.oschinadome.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ziweiyang.toppine.com.oschinadome.R;


/**
 *  底部导航栏的button  --> button上可以显示几条未读信息
 *  自定义的容器--> 下面tab的信息
 */
public class NavigationButton extends FrameLayout {
    private Fragment mFragment = null;
    private Class<?> mClx;
    private ImageView mIconView;    //控制图片的信息
    private TextView mTitleView;    //显示下面的文字
    private TextView mDot;          //显示来的信息数量
    private String mTag;

    public NavigationButton(Context context) {
        super(context);
        init();
    }
    public NavigationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public NavigationButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NavigationButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_nav_item, this, true); //依赖本页面填充这个Fragment

        mIconView = (ImageView) findViewById(R.id.nav_iv_icon);
        mTitleView = (TextView) findViewById(R.id.nav_tv_title);
        mDot = (TextView) findViewById(R.id.nav_tv_dot);
    }

    public void setSelected(boolean selected) {
        super.setSelected(selected);
        mIconView.setSelected(selected);
        mTitleView.setSelected(selected);
    }

    public void showRedDot(int count) {
        mDot.setVisibility(count > 0 ? VISIBLE : GONE);
        mDot.setText(String.valueOf(count));
    }

    public void init(@DrawableRes int resId, @StringRes int strId, Class<?> clx) {
        mIconView.setImageResource(resId);
        mTitleView.setText(strId);
        mClx = clx;
        mTag = mClx.getName();
    }

    public Class<?> getClx() {
        return mClx;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    public String getTag() {
        return mTag;
    }


}
