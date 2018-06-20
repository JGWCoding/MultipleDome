package ziweiyang.toppine.com.oschinadome.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;

import net.oschina.common.utils.StreamUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import ziweiyang.toppine.com.oschinadome.AppContext;
import ziweiyang.toppine.com.oschinadome.R;
import ziweiyang.toppine.com.oschinadome.adapter.FragmentPagerAdapter;
import ziweiyang.toppine.com.oschinadome.bean.SubTab;
import ziweiyang.toppine.com.oschinadome.interfaces.OnTabReselectListener;
import ziweiyang.toppine.com.oschinadome.other.AppOperator;
import ziweiyang.toppine.com.oschinadome.ui.activity.MainActivity;
import ziweiyang.toppine.com.oschinadome.ui.view.TabPickerView;
import ziweiyang.toppine.com.oschinadome.utils.TDevice;

/**
 * 动态栏目Fragment     ---> 综合页面的展示 (页面不包含底部tab) 由于没测试接口,无法获取数据
 * 页面有 mLayoutTab(上面的tab) + mViewPager(中间内容展示)-->SubFragment-->BaseRecyclerViewFragment-->EmptyLayout-->显示 测试无后台接口
 * 控制动态栏目的视图变换  ---> 改变上面tab的变换 --> {@link #onTabReselect()
 */

public class DynamicTabFragment extends BaseTitleFragment implements OnTabReselectListener {

    @Bind(R.id.layout_tab)
    TabLayout mLayoutTab;      //上面的tab控制
    @Bind(R.id.view_tab_picker)
    TabPickerView mViewTabPicker; //+ 加号的点击页面  --> 增删改 tab
    @Bind(R.id.view_pager)
    ViewPager mViewPager;       //tab和下面页面联动
    @Bind(R.id.iv_arrow_down)
    ImageView mViewArrowDown;   // + 加号 图片

    private MainActivity activity;
    private Fragment mCurFragment;
    private FragmentPagerAdapter mAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_dynamic_tab;
    }

    private static TabPickerView.TabPickerDataManager mTabPickerDataManager;
    List<SubTab> tabs;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
        activity.addOnTurnBackListener(new MainActivity.TurnBackListener() {    //添加回退键监听
            @Override
            public boolean onTurnBack() {
                return mViewTabPicker != null && mViewTabPicker.onTurnBack();//如果添加栏目是在显示,就隐藏
            }
        });
    }

    public static TabPickerView.TabPickerDataManager initTabPickerManager() { //一开始LunchActivity调用
        if (mTabPickerDataManager == null) {
            mTabPickerDataManager = new TabPickerView.TabPickerDataManager() {
                @Override
                public List<SubTab> setupActiveDataSet() { //选中的激活的栏目
                    FileReader reader = null;
                    try {
                        //     path:/data/data/packageName/files/sub_tab_active.json
                        File file = AppContext.getInstance().getFileStreamPath("sub_tab_active.json");
                        if (!file.exists()) return null;
                        reader = new FileReader(file);
                        return AppOperator.getGson().fromJson(reader,
                                new TypeToken<ArrayList<SubTab>>() {
                                }.getType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        StreamUtil.close(reader);
                    }
                    return null;
                }

                @Override
                public List<SubTab> setupOriginalDataSet() {    //一开始配置基础的原件 栏目
                    InputStreamReader reader = null;
                    try {
                        reader = new InputStreamReader(
                                AppContext.getInstance().getAssets().open("sub_tab_original.json")
                                , "UTF-8");
                        return AppOperator.getGson().<ArrayList<SubTab>>fromJson(reader,
                                new TypeToken<ArrayList<SubTab>>() {
                                }.getType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        StreamUtil.close(reader);
                    }
                    return null;
                }

                @Override
                public void restoreActiveDataSet(List<SubTab> mActiveDataSet) { //重新存储选中的栏目
                    OutputStreamWriter writer = null;
                    try {
                        writer = new OutputStreamWriter(
                                AppContext.getInstance().openFileOutput(
                                        "sub_tab_active.json", Context.MODE_PRIVATE)
                                , "UTF-8");
                        AppOperator.getGson().toJson(mActiveDataSet, writer);
                        AppContext.set("TabsMask", TDevice.getVersionCode());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        StreamUtil.close(writer);
                    }
                }
            };
        }
        return mTabPickerDataManager;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        //  设置监听
        mViewTabPicker.setTabPickerManager(initTabPickerManager());
        mViewTabPicker.setOnTabPickingListener(new TabPickerView.OnTabPickingListener() {

            private boolean isChangeIndex = false;

            @Override
            @SuppressWarnings("all")
            public void onSelected(final int position) {
                final int index = mViewPager.getCurrentItem();
                mViewPager.setCurrentItem(position);
                if (position == index) {
                    mAdapter.commitUpdate();
                    // notifyDataSetChanged为什么会导致TabLayout位置偏移，而且需要延迟设置才能起效？？？
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLayoutTab.getTabAt(position).select();
                        }
                    }, 50);
                }
            }

            @Override
            public void onRemove(int position, SubTab tab) {
                isChangeIndex = true;
            }

            @Override
            public void onInsert(SubTab tab) {
                isChangeIndex = true;
            }

            @Override
            public void onMove(int op, int np) {
                isChangeIndex = true;
            }

            @Override
            public void onRestore(final List<SubTab> mActiveDataSet) {
                if (!isChangeIndex) return;
                AppOperator.getExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        OutputStreamWriter writer = null;
                        try {
                            writer = new OutputStreamWriter(
                                    AppContext.getInstance().openFileOutput(
                                            "sub_tab_active.json", Context.MODE_PRIVATE)
                                    , "UTF-8");
                            AppOperator.getGson().toJson(mActiveDataSet, writer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            StreamUtil.close(writer);
                        }

                        /*String json = AppOperator.getGson().toJson(mActiveDataSet);
                        FileOutputStream fos = null;
                        try {
                            fos = AppContext.getInstance().openFileOutput("sub_tab_active.json",
                                    Context.MODE_PRIVATE);
                            fos.write(json.getBytes("UTF-8"));
                            fos.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            StreamUtil.close(fos);
                        }*/
                    }
                });
                isChangeIndex = false;
                tabs.clear();
                tabs.addAll(mActiveDataSet);
                mAdapter.notifyDataSetChanged();
            }
        });
        //出现的动画
        mViewTabPicker.setOnShowAnimation(new TabPickerView.Action1<ViewPropertyAnimator>() {
            @Override
            public void call(ViewPropertyAnimator animator) {
                mViewArrowDown.setEnabled(false);
                activity.toggleNavTabView(false);
                mViewArrowDown.animate()
                        .rotation(225)
                        .setDuration(380)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                super.onAnimationEnd(animator);
                                mViewArrowDown.setRotation(45);
                                mViewArrowDown.setEnabled(true);
                            }
                        }).start();

            }
        });
        //隐藏的动画
        mViewTabPicker.setOnHideAnimator(new TabPickerView.Action1<ViewPropertyAnimator>() {
            @Override
            public void call(ViewPropertyAnimator animator) {
                mViewArrowDown.setEnabled(false);
                activity.toggleNavTabView(true);
                mViewArrowDown.animate()
                        .rotation(-180)
                        .setDuration(380)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                super.onAnimationEnd(animator);
                                mViewArrowDown.setRotation(0);
                                mViewArrowDown.setEnabled(true);
                            }
                        });
            }
        });

        tabs = new ArrayList<>();
        tabs.addAll(mViewTabPicker.getTabPickerManager().getActiveDataSet());
        for (SubTab tab : tabs) {
            mLayoutTab.addTab(mLayoutTab.newTab().setText(tab.getName()));//设置感兴趣的栏目
        }

        mViewPager.setAdapter(mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return SubFragment.newInstance(getContext(), tabs.get(position));   //返回这个Fragment视图
            }

            @Override
            public int getCount() {
                return tabs.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabs.get(position).getName();
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);
                if (mCurFragment == null) {
                    commitUpdate();
                }
                mCurFragment = (Fragment) object;
            }

            //this is called when notifyDataSetChanged() is called
            @Override
            public int getItemPosition(Object object) {
                return PagerAdapter.POSITION_NONE;
            }

        });
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mAdapter.commitUpdate();
                }
            }
        });
        mLayoutTab.setupWithViewPager(mViewPager);  //关联这个ViewPager
        mLayoutTab.setSmoothScrollingEnabled(true); //设置可滑动的
    }


    @Override
    protected int getTitleRes() {
        return R.string.main_tab_name_news;
    }

    @OnClick(R.id.iv_arrow_down)
    void onClickArrow() { // + 加号的点击事件
        if (mViewArrowDown.getRotation() != 0) {
            mViewTabPicker.onTurnBack();
        } else {
            mViewTabPicker.show(mLayoutTab.getSelectedTabPosition());
        }
    }

    @Override
    protected int getIconRes() {
        return R.mipmap.btn_search_normal;
    }

    @Override
    protected View.OnClickListener getIconClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SearchActivity.show(getContext()); //TODO 这个没有
            }
        };
    }


    @Override
    public void onTabReselect() {   //在MainActivity里面的onReselect方法调用
        if (mCurFragment != null && mCurFragment instanceof OnTabReselectListener) {
            ((OnTabReselectListener) mCurFragment).onTabReselect();
        }
    }
}
