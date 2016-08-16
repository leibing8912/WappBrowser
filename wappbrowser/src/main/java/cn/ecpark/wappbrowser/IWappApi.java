package cn.ecpark.wappbrowser;

/**
 * @className: IWappApi
 * @classDescription: Wapp接口定义
 * @author: swallow
 * @createTime: 2016/4/20
 */
interface IWappApi {
    //Hosts
    String SHOW_ALERT = "alert.widget.native";
    String SHOW_IMG = "img.widget.native";
    String WX_SHARE = "wx_share.function.native";
    String USER_INFO = "user_info.function.native";
    String LOGOUT = "log_out.function.native";
    String KICKOUT = "kickout.function.native";
    String REQUEST = "request.function.native";
    String OPEN = "open.control.native";
    String CLOSE = "close.control.native";
    String GO_BACK = "back.control.native";
    String GO_HOME = "home.control.native";
    String OPEN_MAP = "map.control.native";

    boolean showAlert(String title, String message, String cancel, String[] btns, String cb);
    boolean showImg(String imgUrl);
    boolean wxShare(int type, String cb);
    boolean getUserInfo(String cb);
    boolean logout();
    boolean kickout(String message);
    boolean request(String path, String paramJson, String cb);
    boolean pageOpen(int type, String title, String url);
    boolean close();
    boolean goBack();
    boolean goHome();
    boolean openMap(String address);

    boolean fireReadyEvent();
    boolean fireErrorEvent(String message, String where);

    void onResume();
    void onPaused();

    boolean interceptUrl(String url);
}
