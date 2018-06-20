package ziweiyang.toppine.com.oschinadome.other;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import ziweiyang.toppine.com.oschinadome.bean.Tweet;
import ziweiyang.toppine.com.oschinadome.other.gson.DoubleJsonDeserializer;
import ziweiyang.toppine.com.oschinadome.other.gson.FloatJsonDeserializer;
import ziweiyang.toppine.com.oschinadome.other.gson.ImageJsonDeserializer;
import ziweiyang.toppine.com.oschinadome.other.gson.IntegerJsonDeserializer;
import ziweiyang.toppine.com.oschinadome.other.gson.StringJsonDeserializer;
import ziweiyang.toppine.com.oschinadome.utils.AccountHelper;
import ziweiyang.toppine.com.oschinadome.utils.thread.ThreadProxy;

/**
 * Created by JuQiu
 * on 16/6/24.
 */
public final class AppOperator {
    private static ThreadPoolExecutor EXECUTORS_INSTANCE;      //线程池
    private static Gson GSON_INSTANCE;

    public static Executor getExecutor() {
        if (EXECUTORS_INSTANCE == null) {
            synchronized (AppOperator.class) {
                if (EXECUTORS_INSTANCE == null) {
//                    EXECUTORS_INSTANCE = Executors.newFixedThreadPool(6); //创建有6个固定线程的线程池,任务多会缓存
                    ThreadProxy.initThreadPoolExecutor();
                    EXECUTORS_INSTANCE = ThreadProxy.mExecutor;//有核心线程和最大线程数,任务如果多会创建到最大线程并缓存任务
                }
            }
        }
        return EXECUTORS_INSTANCE;
    }

    public static void runOnThread(Runnable runnable) {
        getExecutor().execute(runnable);
    }

    public static GlideUrl getGlideUrlByUser(String url) {
        if (AccountHelper.isLogin()) {
            return new GlideUrl(url,
            new  LazyHeaders.Builder()
                            .addHeader("Cookie", AccountHelper.getCookie())
                            .build());
        } else {
            return new GlideUrl(url);
        }
    }

    public static Gson createGson() {   //不知道是2.8版本gson固定写法,还是为了提供效率
        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setExclusionStrategies(new SpecificClassExclusionStrategy(null, Model.class));
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        //deserializer  串-并行变换器
        JsonDeserializer deserializer = new IntegerJsonDeserializer();
        gsonBuilder.registerTypeAdapter(int.class, deserializer);
        gsonBuilder.registerTypeAdapter(Integer.class, deserializer);

        deserializer = new FloatJsonDeserializer();
        gsonBuilder.registerTypeAdapter(float.class, deserializer);
        gsonBuilder.registerTypeAdapter(Float.class, deserializer);

        deserializer = new DoubleJsonDeserializer();
        gsonBuilder.registerTypeAdapter(double.class, deserializer);
        gsonBuilder.registerTypeAdapter(Double.class, deserializer);

        deserializer = new StringJsonDeserializer();
        gsonBuilder.registerTypeAdapter(String.class, deserializer);

        gsonBuilder.registerTypeAdapter(Tweet.Image.class, new ImageJsonDeserializer());       //gson自定义对象反序列化

        return gsonBuilder.create();
    }

    public synchronized static Gson getGson() {
        if (GSON_INSTANCE == null)
            GSON_INSTANCE = createGson();
        return GSON_INSTANCE;
    }

}
