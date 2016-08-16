package cn.ecpark.wappbrowser;

import android.app.Activity;
import android.content.Intent;

/**
 * @className: WappAdapter
 * @classDescription: 浏览器适配器，用于BottomView的传递与互动
 * @author: swallow
 * @createTime: 2016/4/13
 */
abstract public class WappAdapter {

    /**
     * 当一个新的窗口创建时，将这个窗口的Activity实例传递给开发者，以便开发者统一管理应用的Activity
     * @param activity
     * @return
     */
    abstract public void onWindowCreate(Activity activity);

    /**
     * 映射Activity Resume
     * @param activity
     */
    abstract public void onWindowResume(Activity activity);

    /**
     * 映射Activity stop
     * @param activity
     */
    abstract public void onWindowPaused(Activity activity);

    /**
     * 当一个窗口被销毁时，调用这个接口，以便开发者统一管理Activity
     * @param activity
     * @return
     */
    abstract public void onWindowClosed(Activity activity);

    /**
     * 映射Activity result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    abstract public void onWindowResult(int requestCode, int resultCode, Intent data);

    /**
     * 实现大图显示
     * @param url 大图地址
     * @return
     */
    abstract public boolean showImage(String url);

    /**
     * 显示对话框
     * @param title 对话框标题
     * @param message 对话框消息
     * @param cancel 取消按钮文案
     * @param btns 其它按键
     * @param cb 回调，将用户点击的button index回传
     * @return
     */
    abstract public boolean showAlert(
            String title,
            String message,
            String cancel,
            String[] btns,
            AlertCallback cb
    );

    /**
     * 实现本地的分享支持
     * @param type 分享类型
     * @param callback 分享结果回调
     * @return false = 未处理， true = 已处理
     */
    abstract public boolean doShare(int type, ShareCallback callback);

    /**
     * 获取用户信息
     * @author swallow
     * @param callback 获取用户信息结果回调
     * @return false = 未处理， true = 已处理
     */
    abstract public boolean getUserInfo(UserInfoCallback callback);

    /**
     * 退出登录
     * @author swallow
     * @param
     * @return false = 未处理， true = 已处理
     */
    abstract public boolean logout();

    /**
     * 踢下线
     * @author swallow
     * @param message 踢下线的信息
     * @return false = 未处理， true = 已处理
     */
    abstract public boolean kickout(String message);

    /**
     * 发起一个请求
     * @param path 请求路径
     * @param paramJson 请求参数
     * @param cb 请求回调
     * @return
     */
    abstract public boolean request(String path, String paramJson, RequestCallback cb);

    /**
     * 返回主页
     * @author swallow
     * @param
     * @return false = 未处理， true = 已处理
     */
    abstract public boolean goHome();

    /**
     * 打开地图
     * @author swallow
     * @param
     * @return false = 未处理， true = 已处理
     */
    abstract public boolean openMap(String address);

    /**
     * 更新预加载库
     * @param feature 现有预加载文件的特征码
     * @return
     */
    abstract public boolean updateProload(String feature, PreloadUpdateCallback cb);

    /**
     * 未支持的uri通过该方法分发给开发者进行处理
     * @param url 未支持的url
     * @return
     */
    abstract public boolean interceptUrl(String url, AfterHandle cb);

    /**
     * 对话框回调
     * @author swallow
     * @since 2016.5.20
     */
    public interface AlertCallback {
        void callback(int index);
    }

    /**
     * 分享回调
     * @author swallow
     * @since 2016/4/22
     */
    public interface ShareCallback {
        void callback();
    }

    /**
     * 获取用户信息回调
     * @author: swallow
     * @since 2016/4/22
     */
    public interface UserInfoCallback {
        void callback(String json);
    }

    /**
     * 请求回调，将请求的返回解析成一个json字符串返回
     * @author swallow
     * @since 2016.4.27
     */
    public interface RequestCallback {
        void callback(String resJson);
    }

    /**
     * 预加载本地库更新回调，开发者下载完最新库到指定目录后通过之回调能知WappBrowser
     * 进行进一步处理
     * @author swallow
     * @since 2016.4.28
     */
    public interface PreloadUpdateCallback {
        void callback(String feature, String path);
    }

    /**
     * 开发者处理完之后重新回传给WappBrowser处理
     * @author swallow
     * @since 2016.5.17
     */
    public interface AfterHandle {
        void turnTo(String url);
    }
}
