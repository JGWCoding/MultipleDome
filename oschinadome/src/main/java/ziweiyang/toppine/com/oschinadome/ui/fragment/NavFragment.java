package ziweiyang.toppine.com.oschinadome.ui.fragment;


import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import net.oschina.common.widget.drawable.shape.BorderShape;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import ziweiyang.toppine.com.oschinadome.R;
import ziweiyang.toppine.com.oschinadome.notice.NoticeBean;
import ziweiyang.toppine.com.oschinadome.notice.NoticeManager;
import ziweiyang.toppine.com.oschinadome.ui.view.NavigationButton;

/**
 * 底部导航栏  NavFragment绑定四个类进行管理  --- 提供底部导航栏视图
 * {@link DynamicTabFragment 综合{@link TweetViewPagerFragment 动弹 {@link  ExploreFragment 发现   {@link  UserInfoFragment 我的
 * (加号点击事件) TweetPublishActivity.show(getContext(), mRoot.findViewById(R.id.nav_item_tweet_pub));
 *  NoticeManager 通知管理
 */
public class NavFragment extends BaseFragment implements View.OnClickListener, NoticeManager.NoticeNotify {
    @Bind(R.id.nav_item_news)
    NavigationButton mNavNews;
    @Bind(R.id.nav_item_tweet)
    NavigationButton mNavTweet;
    @Bind(R.id.nav_item_explore)
    NavigationButton mNavExplore;
    @Bind(R.id.nav_item_me)
    NavigationButton mNavMe;

    private Context mContext;
    private int mContainerId;
    private FragmentManager mFragmentManager;
    private NavigationButton mCurrentNavButton;
    private OnNavigationReselectListener mOnNavigationReselectListener;

    public NavFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_nav;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        ShapeDrawable lineDrawable = new ShapeDrawable(new BorderShape(new RectF(0, 1, 0, 0)));
        lineDrawable.getPaint().setColor(getResources().getColor(R.color.list_divider_color));
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                new ColorDrawable(getResources().getColor(R.color.white)),
                lineDrawable
        });
        root.setBackgroundDrawable(layerDrawable);
        //综合 绑定  DynamicTabFragment页面(控制上面tab选择)
        mNavNews.init(R.drawable.tab_icon_new,
                R.string.main_tab_name_news,
                DynamicTabFragment.class);

        mNavTweet.init(R.drawable.tab_icon_tweet,
                R.string.main_tab_name_tweet,
                TweetViewPagerFragment.class);

        mNavExplore.init(R.drawable.tab_icon_explore,
                R.string.main_tab_name_explore,
                ExploreFragment.class);

        mNavMe.init(R.drawable.tab_icon_me,
                R.string.main_tab_name_my,
                UserInfoFragment.class);

    }

    @OnClick({R.id.nav_item_news, R.id.nav_item_tweet,
            R.id.nav_item_explore, R.id.nav_item_me,
            R.id.nav_item_tweet_pub})
    @Override
    public void onClick(View v) {   //控制下面tab点击事件
        if (v instanceof NavigationButton) {
            NavigationButton nav = (NavigationButton) v;
            doSelect(nav);
        } else if (v.getId() == R.id.nav_item_tweet_pub) {   //加号的点击事件处理
            //会判断是否登录了,没登录跳登录界面 TODO
//            TweetPublishActivity.show(getContext(), mRoot.findViewById(R.id.nav_item_tweet_pub));
        }
    }

    public void setup(Context context, FragmentManager fragmentManager, int contentId, OnNavigationReselectListener listener) {
        mContext = context;
        mFragmentManager = fragmentManager;
        mContainerId = contentId;
        mOnNavigationReselectListener = listener;

        // do clear
        clearOldFragment();
        // do select first
        doSelect(mNavNews);
    }

    public void select(int index) {
        if (mNavMe != null)
            doSelect(mNavMe);
    }

    @SuppressWarnings("RestrictedApi")
    private void clearOldFragment() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (transaction == null || fragments == null || fragments.size() == 0)
            return;
        boolean doCommit = false;
        for (Fragment fragment : fragments) {
            if (fragment != this && fragment != null) {
                transaction.remove(fragment);
                doCommit = true;
            }
        }
        if (doCommit)
            transaction.commitNow();
    }
    //选择新的tab
    private void doSelect(NavigationButton newNavButton) {
        // If the new navigation is me info fragment, we intercept it
        /*
        if (newNavButton == mNavMe) {
            if (interceptMessageSkip())
                return;
        }
        */
        NavigationButton oldNavButton = null;
        if (mCurrentNavButton != null) {
            oldNavButton = mCurrentNavButton;
            if (oldNavButton == newNavButton) {
                onReselect(oldNavButton);   //当点击的是同一个底部button时,执行子类实现的接口,不走下面方法
                return;
            }
            oldNavButton.setSelected(false);    //设置下setSelected,改变选中的颜色
        }
        newNavButton.setSelected(true);
        doTabChanged(oldNavButton, newNavButton);
        mCurrentNavButton = newNavButton;
    }
    //主要的切换tab控制事件
    private void doTabChanged(NavigationButton oldNavButton, NavigationButton newNavButton) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (oldNavButton != null) {
            if (oldNavButton.getFragment() != null) {
                ft.detach(oldNavButton.getFragment());  //解除绑定这个生命周期
            }
        }
        if (newNavButton != null) {
            if (newNavButton.getFragment() == null) {
                //直接替换当前Fragment
                Fragment fragment = Fragment.instantiate(mContext,
                        newNavButton.getClx().getName(), null);//在 initWidget 方法中初始化了四个Fragment
                ft.add(mContainerId, fragment, newNavButton.getTag());//直接添加这个Fragment
                newNavButton.setFragment(fragment);
            } else {
                ft.attach(newNavButton.getFragment()); //绑定这个生命周期,启动这个Fragment
            }
        }
        ft.commit();
    }

    /**
     * 拦截底部点击，当点击个人按钮时进行消息跳转
     */
    private boolean interceptMessageSkip() {
        NoticeBean bean = NoticeManager.getNotice();
        if (bean.getAllCount() > 0) {
//            if (bean.getLetter() + bean.getMention() + bean.getReview() > 0)
//                UserMessageActivity.show(getActivity());//TODO
//            else
//                UserFansActivity.show(getActivity(), AccountHelper.getUserId());//TODO
            return true;
        }
        return false;
    }

    private void onReselect(NavigationButton navigationButton) {
        OnNavigationReselectListener listener = mOnNavigationReselectListener;
        if (listener != null) {
            listener.onReselect(navigationButton);
        }
    }

    @Override
    public void onNoticeArrived(NoticeBean bean) {
        mNavMe.showRedDot(bean.getAllCount());
    }

    public interface OnNavigationReselectListener {     //下面导航tab重新选择
        void onReselect(NavigationButton navigationButton);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NoticeManager.unBindNotify(this);
    }

    @Override
    protected void initData() {
        super.initData();
        NoticeManager.bindNotify(this);
    }
}
