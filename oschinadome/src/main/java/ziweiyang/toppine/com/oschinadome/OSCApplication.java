package ziweiyang.toppine.com.oschinadome;

import net.oschina.common.helper.ReadStateHelper;

import ziweiyang.toppine.com.oschinadome.net.ApiHttpClient;
import ziweiyang.toppine.com.oschinadome.utils.AccountHelper;
import ziweiyang.toppine.com.oschinadome.utils.cache.DetailCache;
import ziweiyang.toppine.com.oschinadome.utils.db.DBManager;

/**
 * Created by Administrator on 2018/5/23.
 */

public class OSCApplication extends AppContext {

    private static final String CONFIG_READ_STATE_PRE = "CONFIG_READ_STATE_PRE_";

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化操作
        DetailCache.init(getApplicationContext()); //缓存设置  cache文件超时进行删除
        init();
    }

    public static void reInit() {
        ((OSCApplication) OSCApplication.getInstance()).init();
    }

    private void init() {
        // 初始化异常捕获类
        AppCrashHandler.getInstance().init(this);
        // 初始化账户基础信息    --- User的SharedPreferences是否有账户信息
        AccountHelper.init(this);
        // 初始化网络请求
        ApiHttpClient.init(this);
        //初始化百度地图
//        SDKInitializer.initialize(this);    //TODO 注释
        DBManager.init(this); //数据库
    }

    /**
     * 获取已读状态管理器
     *
     * @param mark 传入标示，如：博客：blog; 新闻：news
     * @return 已读状态管理器
     */
    public static ReadState getReadState(String mark) {
        ReadStateHelper helper = ReadStateHelper.create(getInstance(),
                CONFIG_READ_STATE_PRE + mark, 100);
        return new ReadState(helper);
    }

    /**
     * 一个已读状态管理器
     */
    public static class ReadState {
        private ReadStateHelper helper;

        ReadState(ReadStateHelper helper) {
            this.helper = helper;
        }

        /**
         * 添加已读状态
         *
         * @param key 一般为资讯等Id
         */
        public void put(long key) {
            helper.put(key);
        }

        /**
         * 添加已读状态
         *
         * @param key 一般为资讯等Id
         */
        public void put(String key) {
            helper.put(key);
        }

        /**
         * 获取是否为已读
         *
         * @param key 一般为资讯等Id
         * @return True 已读
         */
        public boolean already(long key) {
            return helper.already(key);
        }

        /**
         * 获取是否为已读
         *
         * @param key 一般为资讯等Id
         * @return True 已读
         */
        public boolean already(String key) {
            return helper.already(key);
        }
    }
}
