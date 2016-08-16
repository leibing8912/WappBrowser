package cn.ecpark.wapptest;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import cn.ecpark.wappbrowser.WappBrowser;

/**
 * Created by Administrator on 2016/4/21.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        WappBrowser.getInstance().init(this, new MyAdapter(this));
    }
}
