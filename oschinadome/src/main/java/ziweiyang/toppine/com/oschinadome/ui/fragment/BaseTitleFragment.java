package ziweiyang.toppine.com.oschinadome.ui.fragment;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewStub;

import ziweiyang.toppine.com.oschinadome.R;
import ziweiyang.toppine.com.oschinadome.ui.view.TitleBar;


/**
 * 强制子类填充内容布局,自带TitleBar
 * 标题视图已给样式,给予子类填充标题文字
 */
public abstract class BaseTitleFragment extends BaseFragment {

    TitleBar mTitleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_title;
    }

    @Override
    protected void onBindViewBefore(View root) {
        super.onBindViewBefore(root);
        // on before onBindViewBefore call
        ViewStub stub = (ViewStub) root.findViewById(R.id.lay_content);
        stub.setLayoutResource(getContentLayoutId());
        stub.inflate();
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        // not null
        mTitleBar = (TitleBar) root.findViewById(R.id.nav_title_bar);
        mTitleBar.setTitle(getTitleRes());
        mTitleBar.setIcon(getIconRes());
        mTitleBar.setIconOnClickListener(getIconClickListener());
    }

    protected abstract
    @LayoutRes
    int getContentLayoutId();   //添加内容布局

    protected abstract
    @StringRes
    int getTitleRes();

    protected
    @DrawableRes
    int getIconRes() {
        return 0;
    }

    protected View.OnClickListener getIconClickListener() {
        return null;
    }
}
