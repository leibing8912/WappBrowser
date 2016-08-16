# WappBrowser
WappBrowser项目说明文档
WappBrowser是一个Android In-App浏览器，这个浏览器遵循亚美H5框加规范，规范主要将H5 页面分成了三类：

Type	Jump to native page	Jump to H5 page
External	No	Yes
WebApp	Yes	Yes
App	Yes	No
该浏览器配合Wapp Js SDK使用，可以使得页面能够获取一 些本地功能的支持。

如何使用

初始化
    public class MyApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            WappBrowser.getInstance().init(this, new MyAdapter(this));
        }
    }
打开页面
    WappParams params = new WappParams.Builder()
                            .setType(WappParams.BROWSE_TYPE_APP)
                            .setTitle("     ")
                            .setUrl("http://10.1.1.86:8080/Wapp/test.html")
                            .build();
    WappBrowser.getInstance().openPage(this, params);
