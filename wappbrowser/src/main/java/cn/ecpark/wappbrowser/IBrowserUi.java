package cn.ecpark.wappbrowser;

import android.content.Context;

/**
 * 更新View接口
 * @author swallow
 * @since 2016.5.20
 */
public interface IBrowserUi {
    Context getContext();
    void setTitle(String title);
    void updateProgress(int progress);
    void showErrorView(boolean shouldShow);
    void openUrl(String url) throws Throwable;
    void goBack();
    void close();
}
