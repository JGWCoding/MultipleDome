package ziweiyang.toppine.com.oschinadome;

import java.util.Properties;

import ziweiyang.toppine.com.oschinadome.base.BaseApplication;
import ziweiyang.toppine.com.oschinadome.utils.db.DBManager;
import ziweiyang.toppine.com.oschinadome.utils.cache.DataCleanManager;

public class AppContext extends BaseApplication {

    public static final int PAGE_SIZE = 20;// 默认分页大小
    private static AppContext instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DBManager.init(this);   //初始化osc_data.db管理器
    }

    /**
     * 获得当前app运行的AppContext
     *
     * @return AppContext
     */
    public static AppContext getInstance() {
        return instance;
    }

    public Properties getProperties() {     //获取配置文件里的配置       -- 跟SharedPreferences差不多
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) { //设置 --配置文件 key-value
        AppConfig.getAppConfig(this).set(key, value);
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        //DataCleanManager.cleanDatabases(this);
        // 清除数据缓存
        DataCleanManager.cleanInternalCache(this);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {//大于SDK_8的进行删除
            DataCleanManager.cleanCustomCache(getExternalCacheDir());
        }
        /*
        Run.onUiSync(new Action() {
            @Override
            public void call() {
                // Glide 清理内存必须在主线程
                Glide.get(OSCApplication.getInstance()).clearMemory();
            }
        });

        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                // Glide 清理磁盘必须在子线程
                Glide.get(OSCApplication.getInstance()).clearDiskCache();
            }
        });
        */

        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
    }

    public static void setLoadImage(boolean flag) {
        set(AppConfig.KEY_LOAD_IMAGE, flag);
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }
}