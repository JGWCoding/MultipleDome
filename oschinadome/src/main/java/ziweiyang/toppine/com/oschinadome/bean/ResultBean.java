package ziweiyang.toppine.com.oschinadome.bean;

import ziweiyang.toppine.com.oschinadome.notice.NoticeBean;
import ziweiyang.toppine.com.oschinadome.notice.NoticeManager;

/**
 * Created by huanghaibin
 * on 16-5-23.
 */
public class ResultBean<T> {

    public static final int RESULT_SUCCESS = 1; //成功
    public static final int RESULT_UNKNOW = 0;  //未知
    public static final int RESULT_ERROR = -1;  //错误
    public static final int RESULT_NOT_FIND = 404;  //找不到
    public static final int RESULT_NOT_LOGIN = 201; //未登陆
    public static final int RESULT_TOKEN_EXPRIED = 202; //token重新发送
    public static final int RESULT_NOT_PERMISSION = 203;    //没权限
    public static final int RESULT_TOKEN_ERROR = 204;   //token错误

    private T result;   //信息bean
    private int code;   //返回成功状态码
    private String message; //信息
    private String time;    //时间
    private NoticeBean notice;  //通知

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isOk() {     //是否请求成功
        return code == RESULT_SUCCESS;
    }

    public boolean isSuccess() {
        // 每次回来后通知消息到达
        NoticeManager.publish(this, this.notice);
        return code == RESULT_SUCCESS && result != null;
    }

    public NoticeBean getNotice() {
        return notice;
    }

    public void setNotice(NoticeBean notice) {
        this.notice = notice;
    }

    @Override
    public String toString() {
        return "code:" + code
                + " + message:" + message
                + " + time:" + time
                + " + result:" + (result != null ? result.toString() : null);
    }
}
