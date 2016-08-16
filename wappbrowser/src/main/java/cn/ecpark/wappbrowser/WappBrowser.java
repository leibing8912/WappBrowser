package cn.ecpark.wappbrowser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * @className: WappBrowser
 * @classDescription: 亚美App内嵌浏览器, 该浏览器有如下功能：
 *      打开一个页面;
 *      支持deeplink;
 *      支持ym_browser_sdk.js;
 *      支持预加载
 * @author: swallow
 * @createTime: 2016/4/13
 */
public class WappBrowser {
    //Singleton
    private static WappBrowser instance;
    public static WappBrowser getInstance() {
        if (instance == null)
            instance = new WappBrowser();
        return instance;
    }

    private WappBrowser(){}

    //Variables
    private WappAdapter mAdapter;

    /**
     * 初始化，检查并更新页面包
     * @author swallow
     * @createTime 2016/4/13
     * @lastModify 2016/4/13
     * @param
     * @return
     */
    public void init(Context context, WappAdapter adapter) {
        mAdapter = adapter;
        PreloadController controller = PreloadController.getInstance();
        controller.init(context);
        controller.askUpdate(adapter);
    }

    /**
     * 打开一个有BottomView的页面
     * @author swallow
     * @createTime 2016/4/13
     * @lastModify 2016/4/13
     * @param context Context
     * @param params 浏览器参数，包括了浏览类型，地址，title, bottomView等
     * @return
     */
    public void openPage(Context context, WappParams params) {
        Intent intent = null;
        if (params.mType == WappParams.BROWSE_TYPE_NATIVE) {
            //打开本地页
            intent = new Intent(WappParams.YM_DL_ACTION, Uri.parse(params.mUrl));
        } else {
            //用浏览器打开一个页面
            intent = new Intent(context, WappBrowserActivity.class);
            intent.putExtra("params", params.toJson());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        //尝试打开页面
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开一个本地页面
     * @param context
     * @param url 本地页面地址
     */
    public void openNativePage(Context context, String url) {
        try {
            Intent intent = new Intent(WappParams.YM_DL_ACTION, Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取BrowserAdapter
     * @author swallow
     * @createTime 2016/4/13
     * @lastModify 2016/4/13
     * @return
     */
    public WappAdapter getAdapter() {
        return mAdapter;
    }
}
