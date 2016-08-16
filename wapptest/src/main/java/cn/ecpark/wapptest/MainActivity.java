package cn.ecpark.wapptest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.ecpark.wappbrowser.WappBrowser;
import cn.ecpark.wappbrowser.WappParams;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    private Button btnOpenPage;
    private Button btnBottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenPage = (Button) findViewById(R.id.btn_open_page);
        btnBottomView = (Button) findViewById(R.id.btn_test_bottom_view);

        btnOpenPage.setOnClickListener(this);
        btnBottomView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_page:
                WappParams params = new WappParams.Builder()
                        .setType(WappParams.BROWSE_TYPE_EXTERN)
                        .setTitle("     ")
                        .setUrl("http://10.1.1.248/test.html")
                        .build();
                WappBrowser.getInstance().openPage(this, params);
                break;
            case R.id.btn_test_bottom_view:
                params = new WappParams.Builder()
                        .setType(WappParams.BROWSE_TYPE_EXTERN)
                        .setTitle("FuckingTitle")
                        .setUrl("http://10.1.1.248/test.html")
                        .build();
                WappBrowser.getInstance().openPage(this, params);
                break;
            default:
                break;
        }
    }


}
