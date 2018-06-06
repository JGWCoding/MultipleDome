package ziweiyang.toppine.com.oschinadome.utils.cache;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import net.oschina.common.utils.StreamUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import ziweiyang.toppine.com.oschinadome.other.AppOperator;
import ziweiyang.toppine.com.oschinadome.utils.TDevice;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;
import static com.google.gson.internal.$Gson$Types.canonicalize;
import static com.google.gson.internal.$Gson$Types.typeToString;

public class CacheManager {

    // wifi缓存时间为5分钟
    private static long wifi_cache_time = 5 * 60 * 1000;
    // 其他网络环境为1小时
    private static long other_cache_time = 60 * 60 * 1000;

    /**
     * 保存对象
     *
     * @param ser
     * @param file
     * @throws IOException
     */
    public static boolean saveObject(Context context, Serializable ser,
                                     String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            StreamUtil.close(fos, oos);
        }
    }

    public static void deleteObject(Context context, String fileName) {
        String filePath = context.getFilesDir().getPath() + "/" + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 读取对象
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Serializable readObject(Context context, String file) {
        if (!isExistDataCache(context, file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            // 反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = context.getFileStreamPath(file);
                data.delete();
            }
        } finally {
            StreamUtil.close(ois, fis);
        }
        return null;
    }

    /**
     * 判断缓存是否存在
     *
     * @param cachefile
     * @return
     */
    public static boolean isExistDataCache(Context context, String cachefile) {
        if (context == null)
            return false;
        boolean exist = false;
        File data = context.getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }

    /**
     * 判断缓存是否已经失效
     */
    public static boolean isCacheDataFailure(Context context, String cachefile) {
        File data = context.getFileStreamPath(cachefile);
        if (!data.exists()) {

            return false;
        }
        long existTime = System.currentTimeMillis() - data.lastModified();
        boolean failure = false;
        if (TDevice.isWifiOpen()) {
            failure = existTime > wifi_cache_time;
        } else {
            failure = existTime > other_cache_time;
        }
        return failure;
    }

    public static <T> boolean saveToJson(Context context, String fileName, List<T> list) {
        if (context == null || list == null)
            return false;
        String path = context.getCacheDir() + "/" + fileName + ".json";
        File file = new File(path);
        if (list.size() == 0) {
            return !file.exists() || file.delete();
        }

        try {
            return !(!file.exists() && !file.createNewFile())
                    && save(file, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * save cache file
     *
     * @param file file
     * @param list list
     */
    private static <T> boolean save(File file, List<T> list) {
        Writer writer = null;
        try {
            writer = new FileWriter(file);
            AppOperator.getGson().toJson(list, writer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(writer);
        }
        return false;
    }

    public static boolean saveToJson(Context context, String fileName, Object object) {
        String json = new Gson().toJson(object);
        String path = context.getCacheDir() + "/" + fileName;
        File file = new File(path);
        FileOutputStream os = null;
        try {
            if (!file.exists() && !file.createNewFile())
                return false;
            os = new FileOutputStream(file);
            os.write(json.getBytes("utf-8"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(os);
        }
        return false;
    }

    public static <T> T readFromJson(Context context, String fileName, Class cla) {
        return readJson(context, fileName, cla);
    }

    public static <T> T readListJson(Context context, String fileName, Class clx) {
        if (clx == null || TextUtils.isEmpty(fileName)) return null;
        Type type = getListType(clx);
        return readJson(context, fileName, type);
    }

    public static <T> T readJson(Context context, String fileName, Type clx) {
        if (clx == null || context == null) return null;
        String path = context.getCacheDir() + "/" + fileName + ".json";
        File file = new File(path);
        if (!file.exists())
            return null;
        Reader reader = null;
        try {
            reader = new FileReader(file);
            return AppOperator.getGson().fromJson(reader, clx);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(reader);
        }
        return null;
    }

    private static Type getListType(Class clx) {
        return $Gson$Types.canonicalize((new ParameterizedTypeImpl(null, List.class, clx)));
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;

        public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... typeArguments) {
            // require an owner type if the raw type needs it
            if (rawType instanceof Class<?>) {
                Class<?> rawTypeAsClass = (Class<?>) rawType;
                boolean isStaticOrTopLevelClass = Modifier.isStatic(rawTypeAsClass.getModifiers())
                        || rawTypeAsClass.getEnclosingClass() == null;
                checkArgument(ownerType != null || isStaticOrTopLevelClass);
            }

            this.ownerType = ownerType == null ? null : canonicalize(ownerType);
            this.rawType = canonicalize(rawType);
            this.typeArguments = typeArguments.clone();
            for (int t = 0; t < this.typeArguments.length; t++) {
                checkNotNull(this.typeArguments[t]);
                this.typeArguments[t] = canonicalize(this.typeArguments[t]);
            }
        }

        public Type[] getActualTypeArguments() {
            return typeArguments.clone();
        }

        public Type getRawType() {
            return rawType;
        }

        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof ParameterizedType
                    && $Gson$Types.equals(this, (ParameterizedType) other);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(typeArguments)
                    ^ rawType.hashCode()
                    ^ (ownerType != null ? ownerType.hashCode() : 0);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(30 * (typeArguments.length + 1));
            stringBuilder.append(typeToString(rawType));

            if (typeArguments.length == 0) {
                return stringBuilder.toString();
            }

            stringBuilder.append("<").append(typeToString(typeArguments[0]));
            for (int i = 1; i < typeArguments.length; i++) {
                stringBuilder.append(", ").append(typeToString(typeArguments[i]));
            }
            return stringBuilder.append(">").toString();
        }

        private static final long serialVersionUID = 0;
    }
}
