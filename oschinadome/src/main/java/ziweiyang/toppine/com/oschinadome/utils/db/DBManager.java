package ziweiyang.toppine.com.oschinadome.utils.db;

import android.content.Context;
import android.os.Environment;

import net.oschina.common.utils.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ziweiyang.toppine.com.oschinadome.AppContext;

/**
 * 数据库管理对象,
 */

public class DBManager extends DBHelper {

    private DBManager(Context context) {
        super(context);
    }

    private static DBManager mInstance;
    private static DBManager mCountryManager;

    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new DBManager(context);
        }
        if (mCountryManager == null) {
            mCountryManager = getAssetSQLite(context);
        }
    }

    private DBManager(Context context, String name) {
        super(context, name);
    }

    public static DBManager getInstance() {
        return mInstance;
    }

    //得到asset里面的data.db数据库(安装到手机里会进行copy变成osc_data.db),
    public static DBManager getCountryManager() {
        if (mCountryManager == null) {
            mCountryManager = getAssetSQLite(AppContext.getInstance());
        }
        return mCountryManager;
    }

    /**
     * 打开assets的数据库
     *
     * @param context context
     * @return SQLiteDatabase
     */
    private static DBManager getAssetSQLite(Context context) {
        try {
            String path = Environment.getDataDirectory().getAbsolutePath() + "/data/" + context.getPackageName() + "/databases/osc_data.db";
            if (!new File(path).exists()) {
                InputStream is = context.getAssets().open("data.db");
                inputStreamToFile(is, path);//写入数据库到手机设备上
            }
            DBManager manager = new DBManager(context, "osc_data.db");
            if (!manager.isExist("city")) { //data.db(osc_data)里得到city表
                context.deleteDatabase("osc_data.db");
                InputStream is = context.getAssets().open("data.db");
                inputStreamToFile(is, path);
            }
            return new DBManager(context, "osc_data.db");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static void inputStreamToFile(InputStream is, String newPath) {
        FileOutputStream fs = null;
        try {
            int read;
            fs = new FileOutputStream(newPath);
            byte[] buffer = new byte[1444];
            while ((read = is.read(buffer)) != -1) {
                fs.write(buffer, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(fs, is);
        }
    }
}
