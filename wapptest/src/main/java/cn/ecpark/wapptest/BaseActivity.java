package cn.ecpark.wapptest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Stack;

/**
 * Activity基类
 * @author swallow
 * @since 2016.5.10
 */
public class BaseActivity extends AppCompatActivity {
    private static Stack<BaseActivity> activities = new Stack<>();

    public static BaseActivity getCurrentActivity(){
        return activities.peek();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activities.push(this);
    }

    @Override
    protected void onDestroy() {
        activities.pop();
        super.onDestroy();
    }
}
