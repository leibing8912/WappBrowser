package cn.ecpark.wappbrowser;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * Wapp本地支持类，为wapp的运行提供支持
 * @author swallow
 * @since 2016.4.20
 */
class WappSupport implements IWappApi {
    //Constants
    private static final String TAG = "WappSupport";
    public static final String SCHEME = "wapp";

    private int mType = WappParams.BROWSE_TYPE_EXTERN;
    private WappAdapter mAdapter = WappBrowser.getInstance().getAdapter();
    private IBrowserUi mUi;

    private ArrayList<String> mDlWhiteNames = new ArrayList<>();

    /**
     * 构建一个WappSupport实例
     * @param type
     */
    public WappSupport(int type, IBrowserUi uiInterface) {
        mType = type;
        mUi = uiInterface;
        mDlWhiteNames.add("http");
        mDlWhiteNames.add("https");
    }

    public void onWindowCreate(WappBrowserActivity activity) {
        mAdapter.onWindowCreate(activity);
    }

    public void onWindowResume(WappBrowserActivity activity) {
        onResume();
        mAdapter.onWindowResume(activity);
    }

    public void onWindowPaused(WappBrowserActivity activity) {
        mAdapter.onWindowPaused(activity);
        onPaused();
    }

    public void onWindowClosed(WappBrowserActivity activity) {
        mAdapter.onWindowClosed(activity);
    }

    public void onWindowResult(int requestCode, int resultCode, Intent data) {
        mAdapter.onWindowResult(requestCode, resultCode, data);
    }

    /**
     * 分发一个wapp调用
     * @param url 表示wapp调用的url
     * @return 是否已消费本次调用，false = 未消费，true = 已消费
     */
    public boolean dispatch(String url) {
        //url不合法，不予分发
        if (!Utils.isUrlValid(url))
            return false;

        Uri uri = Uri.parse(url);
        //开始分发
        boolean result = false;
        //拦截Wapp接口
        if (uri.getScheme().equals(SCHEME)) {
            switch (uri.getHost()) {
                case SHOW_ALERT:
                    try {
                        String title = uri.getQueryParameter("title");
                        String message = uri.getQueryParameter("message");
                        String cancel = uri.getQueryParameter("cancel");
                        String buttons = uri.getQueryParameter("buttons");
                        String callback = uri.getQueryParameter("callback");
                        JSONArray ja = new JSONArray(buttons);
                        String[] btnList = new String[ja.length()];
                        for (int i=0; i<btnList.length; i++) {
                            btnList[i] = ja.getString(i);
                        }
                        result = showAlert(title, message, cancel, btnList, callback);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error when get the buttons!");
                    }
                    break;
                case SHOW_IMG:
                    result = showImg(uri.getQueryParameter("url"));
                    break;
                case WX_SHARE:
                    result = wxShare(
                            Integer.parseInt(uri.getQueryParameter("type")),
                            uri.getQueryParameter("callback")
                    );
                    break;
                case USER_INFO:
                    result = getUserInfo(uri.getQueryParameter("callback"));
                    break;
                case LOGOUT:
                    result = logout();
                    break;
                case KICKOUT:
                    result = kickout(uri.getQueryParameter("message"));
                    break;
                case REQUEST:
                    String path = uri.getQueryParameter("path");
                    String paramJson = uri.getQueryParameter("params");
                    String callback = uri.getQueryParameter("callback");
                    result = request(path, paramJson, callback);
                    break;
                case OPEN:
                    result = pageOpen(
                            Integer.parseInt(uri.getQueryParameter("type")),
                            uri.getQueryParameter("title"),
                            uri.getQueryParameter("url")
                    );
                    break;
                case CLOSE:
                    result = close();
                    break;
                case GO_BACK:
                    result = goBack();
                    break;
                case GO_HOME:
                    result = goHome();
                    break;
                case OPEN_MAP:
                    result = openMap(uri.getQueryParameter("address"));
                    break;
                default:
                    break;
            }
        }
        //开发者拦截
        if (!result) {
            result = interceptUrl(url);
        }
        //如果uri没被消费则尝试Deeplink
        if (!result)
            result = supportDeeplink(uri);
        return result;
    }

    /**
     * 对一些特殊链接尝试进行Deeplink
     * @param uri
     * @return
     */
    private boolean supportDeeplink(Uri uri) {
        if (mDlWhiteNames.contains(uri.getScheme()))
            return false;
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mUi.getContext().startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void callJsMethod(String method) {
        try {
            mUi.openUrl("javascript:" + method);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private boolean isThisType(int type) {
        if (type == mType)
            return true;
        return false;
    }

    //Wapp api implement.
    @Override
    public boolean showAlert(String title, String message, String cancel, String[] btns, final String cb) {
        boolean hasIntercept = mAdapter.showAlert(
                title, message, cancel, btns,
                new WappAdapter.AlertCallback() {
                    @Override
                    public void callback(int index) {
                        callJsMethod(cb.replace("index", index+""));
                    }
                }
        );
        if (hasIntercept)
            return true;

        //默认对话框
        final AlertDialog dialog = new AlertDialog.Builder(mUi.getContext()).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_wapp_alert);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_wapp_alert_title);
        TextView tvMsg = (TextView) dialog.findViewById(R.id.tv_wapp_alert_message);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_wapp_alert_cancel);
        Button btn1 = (Button) dialog.findViewById(R.id.btn_wapp_alert_1);
        Button btn2 = (Button) dialog.findViewById(R.id.btn_wapp_alert_2);

        tvTitle.setText(title);
        tvMsg.setText(message);
        btnCancel.setText(cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                callJsMethod(cb.replace("index", "0"));
            }
        });
        if (btns != null && !Utils.isStrEmpty(btns[0])){
            btn1.setText(btns[0]);
            btn1.setVisibility(View.VISIBLE);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    callJsMethod(cb.replace("index", "1"));
                }
            });
        }
        if (btns != null && !Utils.isStrEmpty(btns[1])){
            btn2.setText(btns[1]);
            btn2.setVisibility(View.VISIBLE);
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    callJsMethod(cb.replace("index", "2"));
                }
            });
        }
        return true;
    }

    @Override
    public boolean showImg(String imgUrl) {
        return mAdapter.showImage(imgUrl);
    }

    @Override
    public boolean wxShare(int type, final String cb) {
        //调用开发者的分享实现，如果开发者未实现则会返回false
        return mAdapter.doShare(type, new WappAdapter.ShareCallback() {
            @Override
            public void callback() {
                callJsMethod(cb);
            }
        });
    }

    @Override
    public boolean getUserInfo(final String cb) {
        //调用开发者的发享实现，如果开发者未实现则会返回false
        return mAdapter.getUserInfo(new WappAdapter.UserInfoCallback() {
            @Override
            public void callback(String json) {
                callJsMethod(cb.replace("info", json));
            }
        });
    }

    @Override
    public boolean logout() {
        //调用开发者的发享实现，如果开发者未实现则会返回false
        return mAdapter.logout();
    }

    @Override
    public boolean kickout(String message) {
        //调用开发者的发享实现，如果开发者未实现则会返回false
        return mAdapter.kickout(message);
    }

    @Override
    public boolean request(String path, String paramJson, final String cb) {
        return mAdapter.request(path, paramJson, new WappAdapter.RequestCallback() {
            @Override
            public void callback(String resJson) {
                callJsMethod(cb.replace("response", resJson));
            }
        });
    }

    @Override
    public boolean pageOpen(int type, String title, String url) {
        if (isThisType(WappParams.BROWSE_TYPE_EXTERN)){
            switch (type) {
                case WappParams.BROWSE_TYPE_APP:
                    fireErrorEvent("Can not open <AppPage> in a <ExternPage>", "OpenPage");
                    break;
                case WappParams.BROWSE_TYPE_NATIVE:
                    fireErrorEvent("Can not open <NativePage> in a <ExternPage>", "OpenPage");
                    break;
                case WappParams.BROWSE_TYPE_WEBAPP:
                    fireErrorEvent("Can not open <WebAppPage> in a <ExternPage>", "OpenPage");
                    break;
                case WappParams.BROWSE_TYPE_EXTERN:
                    try {
                        mUi.openUrl(url);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        return false;
                    }
                    break;
            }
        } else {
            switch (type) {
                case WappParams.BROWSE_TYPE_EXTERN:
                case WappParams.BROWSE_TYPE_WEBAPP:
                case WappParams.BROWSE_TYPE_APP:
                    try {
                        WappParams params = new WappParams.Builder()
                                .setType(type)
                                .setTitle(title)
                                .setUrl(url)
                                .build();
                        WappBrowser.getInstance().openPage(mUi.getContext(), params);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    break;
                case WappParams.BROWSE_TYPE_NATIVE:
                    //打开Activity
                    try {
                        Intent intent = new Intent(
                                WappParams.YM_DL_ACTION,
                                Uri.parse(url)
                        );
                        mUi.getContext().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean close() {
        try {
            mUi.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean goBack() {
        mUi.goBack();
        return true;
    }

    @Override
    public boolean goHome() {
        //调用开发者的发享实现，如果开发者未实现则会返回false
        return mAdapter.goHome();
    }

    @Override
    public boolean openMap(String address) {
        //调用开发者的发享实现，如果开发者未实现则会返回false
        return mAdapter.openMap(address);
    }

    @Override
    public boolean fireReadyEvent() {
        callJsMethod("wapp.utils.readyEvent()");
        return true;
    }

    @Override
    public boolean fireErrorEvent(String message, String where) {
        String method = String.format("wapp.utils.errorEvent('%s','%s')", message, where);
        callJsMethod(method);
        return true;
    }

    @Override
    public void onResume() {
        callJsMethod("wapp.utils.resumeEvent()");
    }

    @Override
    public void onPaused() {
        callJsMethod("wapp.utils.pausedEvent");
    }

    @Override
    public boolean interceptUrl(String url) {
        return mAdapter.interceptUrl(url, new WappAdapter.AfterHandle() {
            @Override
            public void turnTo(String url) {
                try {
                    mUi.openUrl(url);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }
}
