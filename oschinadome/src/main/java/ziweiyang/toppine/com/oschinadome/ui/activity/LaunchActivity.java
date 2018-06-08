package ziweiyang.toppine.com.oschinadome.ui.activity;

import android.content.Intent;
import android.text.TextUtils;

import ziweiyang.toppine.com.oschinadome.OSCApplication;
import ziweiyang.toppine.com.oschinadome.R;
import ziweiyang.toppine.com.oschinadome.Setting;
import ziweiyang.toppine.com.oschinadome.bean.User;
import ziweiyang.toppine.com.oschinadome.other.AppOperator;
import ziweiyang.toppine.com.oschinadome.ui.fragment.DynamicTabFragment;
import ziweiyang.toppine.com.oschinadome.utils.AccountHelper;
import ziweiyang.toppine.com.oschinadome.utils.TLog;

/**
 * 应用启动界面  --> 背景是Theme主题里配置好了的
 */
public class LaunchActivity extends BaseActivity {
    @Override
    protected int getContentView() {
        return R.layout.app_start;  //FrameLayout --> 空界面
    }

    @Override
    protected void initData() {
        super.initData();
        // 在这里我们检测是否是新版本安装，如果是则进行老版本数据迁移工作
        // 该工作可能消耗大量时间所以放在自线程中执行
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                doMerge();
            }
        });
    }

    private void doMerge() {
        // 判断是否是新版本
        if (Setting.checkIsNewVersion(this)) {
            //有新版本 Cookie迁移
            String cookie = OSCApplication.getInstance().getProperty("cookie");
            if (!TextUtils.isEmpty(cookie)) {
                OSCApplication.getInstance().removeProperty("cookie");
                User user = AccountHelper.getUser();
                user.setCookie(cookie);
                AccountHelper.updateUserCache(user);    //更新账户信息 添加cookie
                TLog.e("");
                OSCApplication.reInit();    //账户重新加载
            }
        }
        TLog.e("");
        // 栏目(增减)Manager初始化 --- 把tab的信息从asset文件里读取出来(json文件),转换成bean
        DynamicTabFragment.initTabPickerManager();

        // Delay...
        try {
//            Thread.sleep(800);
            Thread.sleep(1);//睡眠一毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 完成后进行跳转操作
        redirectTo();
    }

    private void redirectTo() {
        TLog.e("LaunchActivity跳转到MainActivity");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
