package cn.ecpark.wappbrowser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 业务逻辑处理器
 * @author swallow
 * @since 2016.5.20
 */
class Presenter {
    private IBrowserUi mUi;
    private WappSupport mSupport;
    private boolean mErrorFlag;

    public Presenter(IBrowserUi uiInterface) {
        mUi = uiInterface;
    }

    /**
     * 支持Wapp
     * @param type 支持类别(详见{@link WappParams})
     */
    public void doWappSupport(int type) {
        mSupport = new WappSupport(type, mUi);
    }

    public void onCreate(WappBrowserActivity activity) {
        assert mSupport != null;
        mSupport.onWindowCreate(activity);
    }

    public void onResume(WappBrowserActivity activity) {
        assert mSupport != null;
        mSupport.onWindowResume(activity);
    }

    public void onPaused(WappBrowserActivity activity) {
        assert mSupport != null;
        mSupport.onWindowPaused(activity);
    }

    public void onDestroy(WappBrowserActivity activity) {
        assert mSupport != null;
        mSupport.onWindowClosed(activity);
    }

    public void onResult(int requestCode, int resultCode, Intent data) {
        assert mSupport != null;
        mSupport.onWindowResult(requestCode, resultCode, data);
    }

    public boolean interceptUrl(String url) {
        return mSupport.interceptUrl(url);
    }

    /**
     * 设置WebView
     * @param webView
     */
    public void setWebView(WebView webView, WappParams params) {
        webView.setWebViewClient(new BrowserWebClient(params));
        webView.setWebChromeClient(new BrowserChromeClient());
        WebSettings webSettings = webView.getSettings();
        //启用数据库
        webSettings.setDatabaseEnabled(true);
        String dir = webView.getContext().getDir("database", Context.MODE_PRIVATE).getPath();
        //启用地理定位
        webSettings.setGeolocationEnabled(true);
        //设置定位的数据库路径
        webSettings.setGeolocationDatabasePath(dir);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString(
                webSettings.getUserAgentString()
                        + " WappBrowser/"+BuildConfig.VERSION_NAME
                        + " "
                        + mUi.getContext().getPackageName()
                        + "/"
                        + Utils.getVersionCode(mUi.getContext())
        );
    }

    /**
     * @className: BrowserWebClient
     * @classDescription: 浏览器Client
     * @author: swallow
     * @createTime: 2016/4/13
     */
    private class BrowserWebClient extends WebViewClient {
        private WappParams mParams;

        public BrowserWebClient(WappParams params) {
            mParams = params;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //Wapp调用分发
            Uri uri = Uri.parse(url);
            boolean isDispatched = mSupport.dispatch(url);

            //已处理
            if (isDispatched)
                return true;
            //wapp接口未处理
            else if (uri.getScheme().equals(WappSupport.SCHEME)){
                mSupport.fireErrorEvent("This method has not been support!", uri.getHost());
                return true;
            }
            //当前页面为APP型
            if (mParams.mType == WappParams.BROWSE_TYPE_APP &&
                    !view.getUrl().equals(url)){
                mSupport.fireErrorEvent("App page not support jump!", uri.getHost());
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mErrorFlag = false;
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mUi.setTitle(view.getTitle());
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mErrorFlag = true;
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    /**
     * @className: BrowserChromeClient
     * @classDescription: 浏览器ChromeClient
     * @author: swallow
     * @createTime: 2016/4/13
     */
    private class BrowserChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //更新网页加载进度
            mUi.updateProgress(newProgress);
            //加载完后，如果出错就显示错误页
            if (newProgress >= 100) {
                mUi.showErrorView(mErrorFlag);
                if (!mErrorFlag)
                    mSupport.fireReadyEvent();
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }
}
