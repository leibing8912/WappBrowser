package cn.ecpark.wapptest;

import android.os.Bundle;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 显示大图页面
 * @author swallow
 * @since 2016.5.10
 */
public class ImageActivity extends BaseActivity {
    SimpleDraweeView sdvImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        sdvImage = (SimpleDraweeView) findViewById(R.id.sdv_show_image);
        sdvImage.setImageURI(getIntent().getData());
    }
}
