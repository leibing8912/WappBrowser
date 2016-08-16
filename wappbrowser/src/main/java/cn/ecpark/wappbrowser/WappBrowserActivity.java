package cn.ecpark.wappbrowser;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;


/**
 * @className: WappBrowserActivity
 * @classDescription: 浏览器页面
 * @author: swallow
 * @createTime: 2016/4/13
 */
public class WappBrowserActivity extends Activity implements IBrowserUi {
    private WebView mWvBrowser;
    private ProgressBar mPbBrowser;
    private View mTbBrowser;
    private View mViewError;

    private WappParams mParams;
    private Presenter mPresenter;

    //Toolbar
    private TextView mTvTitle;
    private ImageView mIvLeft;
    private ImageView mIvRight;

    //Close listener
    private View.OnClickListener mCloseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            WappBrowserActivity.this.finish();
        }
    };

    //Refresh listener
    private View.OnClickListener mRefreshListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mWvBrowser.reload();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brower);
        mWvBrowser = (WebView) findViewById(R.id.wv_browser);
        mTbBrowser = findViewById(R.id.rl_wapp_browser_toolbar);
        mPbBrowser = (ProgressBar) findViewById(R.id.pb_browser);
        mViewError = findViewById(R.id.fl_error_view);


        //恢复到被回收前的页面
        if (savedInstanceState != null)
            mParams = WappParams.fromJson(savedInstanceState.getString("params"));
        else
            mParams = getParams(getIntent());

        //逻辑处理器
        mPresenter = new Presenter(this);
        mPresenter.doWappSupport(mParams.mType);

        //初始化WebView
        mPresenter.setWebView(mWvBrowser, mParams);

        //初始化Toolbar
        initToolbar(mParams);

        //打开第一个页面
        try {
            openUrl(mParams.mUrl);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        mPresenter.onCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume(this);
    }

    @Override
    protected void onPause() {
        mPresenter.onPaused(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mParams.mUrl = mWvBrowser.getUrl();
        outState.putString("params", mParams.toJson());
        super.onSaveInstanceState(outState);
    }

    private void initToolbar(WappParams params) {
        mTvTitle = (TextView) mTbBrowser.findViewById(R.id.tv_wapp_browser_title);
        mIvLeft = (ImageView) mTbBrowser.findViewById(R.id.iv_wapp_browser_left);
        mIvRight = (ImageView) mTbBrowser.findViewById(R.id.iv_wapp_browser_right);

        if (!Utils.isStrEmpty(params.mTitle))
            setTitle(params.mTitle);

        //根据H5框架规范，不同类型的页面有不同的toolbar
        switch (params.mType) {
            case WappParams.BROWSE_TYPE_EXTERN:
            case WappParams.BROWSE_TYPE_WEBAPP:
                //外链型和WebApp型,包含多页H5
                mIvLeft.setImageResource(R.mipmap.btn_close_normal);
                break;
            case WappParams.BROWSE_TYPE_APP:
                //H5替代Activity型，包含单页H5
                mIvLeft.setImageResource(R.mipmap.actionbar_back_normal_btn);
                break;
            default:
                break;
        }
        mIvLeft.setOnClickListener(mCloseListener);
        mIvRight.setOnClickListener(mRefreshListener);
    }

    private WappParams getParams(Intent intent) {
        if (intent == null)
            return null;
        String strParams = intent.getStringExtra("params");
        if (strParams == null)
            return null;
        return WappParams.fromJson(strParams);
    }

    @Override
    public void close() {
        this.finish();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setTitle(String title) {
        if (!Utils.isStrEmpty(mParams.mTitle))
            mTvTitle.setText(mParams.mTitle);
        else
            mTvTitle.setText(title);
    }

    @Override
    public void updateProgress(int progress) {
        //平滑的加载进度
        if (progress < 2) {
            mPbBrowser.setProgress(2);
        } else if (progress > mPbBrowser.getProgress()) {
            ObjectAnimator animation = ObjectAnimator.ofInt(mPbBrowser, "progress", progress);
            animation.setDuration(200); // 0.5 second
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        } else {
            mPbBrowser.setProgress(progress);
        }

        if (progress == 100) {
            //进度条优雅的退场
            ObjectAnimator outAni = ObjectAnimator.ofFloat(mPbBrowser, "alpha", 1, 0);
            outAni.setStartDelay(200);
            outAni.setDuration(200);
            outAni.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mPbBrowser.setVisibility(View.GONE);
                    mPbBrowser.setAlpha(1);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            outAni.start();
        } else {
            //进度条加载时始终使其可见
            mPbBrowser.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showErrorView(boolean shouldShow) {
        if (shouldShow)
            mViewError.setVisibility(View.VISIBLE);
        else
            mViewError.setVisibility(View.GONE);
    }

    @Override
    public void openUrl(String url) throws Throwable {
        //调用js方法
        if (url.startsWith("javascript:")){
            mWvBrowser.loadUrl(url);
            return;
        }

        //拦截
        if (mPresenter.interceptUrl(url))
            return;

        //检查url是否合法
        if (!Utils.isUrlValid(url))
            throw new Throwable(String.format("The url: %s is illegal!", url));

        //检查对应的本地文件是否存在
        Uri uri = Uri.parse(url);
        String localPath = PreloadController.LOCAL_PATH + uri.getPath();
        File localFile = new File(localPath);
        if (!Utils.isStrEmpty(uri.getPath()) && localFile.exists()) {
            //加载本地文件
            url = "file://"+localPath;
        }

        //加载url
        mWvBrowser.loadUrl(url);
    }

    @Override
    public void goBack () {
        assert mWvBrowser != null;
        if (mWvBrowser.canGoBack())
            mWvBrowser.goBack();
        else
            close();
    }

    @Override
    public void onBackPressed() {
        //当浏览类型为单页H5时，返回就是关闭该Activity
        if (mParams.mType == WappParams.BROWSE_TYPE_APP){
            this.finish();
            return;
        }

        if (mWvBrowser.canGoBack())
            mWvBrowser.goBack();
        else
            this.finish();
    }
}
