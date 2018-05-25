package ziweiyang.toppine.com.oschinadome.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * 主要用来强制子类实现getContentView()视图
 * {@link #onResume() {@link #onPause() }进行友盟统计
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected RequestManager mImageLoader;
    private boolean mIsDestroy;
    private final String mPackageNameUmeng = this.getClass().getName();
    private Fragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (initBundle(getIntent().getExtras())) {  //永远为true
            setContentView(getContentView());//强制交给子类实现布局

            initWindow(); //空实现,子类可以实现在这个方法 初始化 点东西

            ButterKnife.bind(this); //butterKnife的初始化
            initWidget();  //空实现,交给子类去实现自己的东西
            initData();   //空实现,交给子类
        } else {
            finish();   //销毁自身  -- 不走
        }

        //umeng analytics ---> 友盟统计
        MobclickAgent.setDebugMode(false);
        MobclickAgent.openActivityDurationTrack(false); //跟踪Activity的统计信息
        //设置方案为normal
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }
    //控制Fragment显示和隐藏
    protected void addFragment(int frameLayoutId,Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (fragment.isAdded()) {
                if (mFragment != null) {
                    transaction.hide(mFragment).show(fragment);
                } else {
                    transaction.show(fragment);
                }
            } else {
                if (mFragment != null) {
                    transaction.hide(mFragment).add(frameLayoutId, fragment);
                } else {
                    transaction.add(frameLayoutId, fragment);
                }
            }
            mFragment = fragment;
            transaction.commit();
        }
    }

    protected void replaceFragment(int frameLayoutId, Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(frameLayoutId, fragment);
            transaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.mPackageNameUmeng);
        MobclickAgent.onResume(this);
    }
    //MobclickAgent统计页面
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.mPackageNameUmeng);
        MobclickAgent.onPause(this);
    }

    protected abstract int getContentView();

    protected boolean initBundle(Bundle bundle) {
        return true;
    }

    protected void initWindow() {
    }

    protected void initWidget() {
    }

    protected void initData() {
    }

    public synchronized RequestManager getImageLoader() {
        if (mImageLoader == null)
            mImageLoader = Glide.with(this);
        return mImageLoader;
    }

    @Override
    protected void onDestroy() {
        mIsDestroy = true;
        super.onDestroy();
    }

    public boolean isDestroy() {
        return mIsDestroy;
    }
}
