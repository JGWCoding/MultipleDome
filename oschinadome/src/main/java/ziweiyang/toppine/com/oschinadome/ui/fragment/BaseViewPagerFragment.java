package ziweiyang.toppine.com.oschinadome.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import ziweiyang.toppine.com.oschinadome.R;

/**
 * 主要封装了TabLayout和ViewPager绑定  --->Fragment
 */
public abstract class BaseViewPagerFragment extends BaseTitleFragment {

    @Bind(R.id.tab_nav)
    protected TabLayout mTabNav;    //上面的tab

    @Bind(R.id.base_viewPager)
    protected ViewPager mBaseViewPager; //下面的ViewPager

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_base_viewpager;   //这里面就是TabLayout和ViewPager相连
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        BaseViewPagerAdapter adapter = new BaseViewPagerAdapter(getChildFragmentManager(), getPagers()); //给Viewpager设置
        mBaseViewPager.setAdapter(adapter);
        mTabNav.setupWithViewPager(mBaseViewPager); //tab和ViewPager建立关联关系
        mBaseViewPager.setCurrentItem(0, true);//设置当前显示页面
    }

    protected abstract PagerInfo[] getPagers();

    public static class PagerInfo {
        private String title;   //设置tab上面的标题
        private Class<?> clx;   //tab相连的Fragment类
        private Bundle args;    //相关的信息

        public PagerInfo(String title, Class<?> clx, Bundle args) {
            this.title = title;
            this.clx = clx;
            this.args = args;
        }
    }

    public class BaseViewPagerAdapter extends FragmentPagerAdapter {
        private PagerInfo[] mInfoList;
        private Fragment mCurFragment;

        public BaseViewPagerAdapter(FragmentManager fm, PagerInfo[] infoList) {
            super(fm);
            mInfoList = infoList;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof Fragment) {
                mCurFragment = (Fragment) object;
            }
        }

        public Fragment getCurFragment() {
            return mCurFragment;
        }

        @Override
        public Fragment getItem(int position) {
            PagerInfo info = mInfoList[position];
            return Fragment.instantiate(getContext(), info.clx.getName(), info.args);   //相当于new Fragment,这个有个缓存-->无缓存new
        }

        @Override
        public int getCount() {
            return mInfoList.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mInfoList[position].title;   //设置tab的文字
        }

        @Override
        public int getItemPosition(Object object) { //得到position的标识是否不一样
            return PagerAdapter.POSITION_NONE;
        }
    }
}
