package cn.ecpark.wapptest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

/**
 * This is a deeplinking activity
 * @author swallow
 * @since 2016.4.22
 */
public class DeeplinkingActivity extends BaseActivity {
    private TextView tvDlContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_linking);

        tvDlContent = (TextView) findViewById(R.id.tv_deeplinking_content);
        tvDlContent.setText(getIntent().getData().getQueryParameter("message"));
    }
}
