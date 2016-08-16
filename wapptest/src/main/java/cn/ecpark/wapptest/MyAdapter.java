package cn.ecpark.wapptest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ecpark.wappbrowser.WappAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2016/4/13.
 */
public class MyAdapter extends WappAdapter {
    private static String LOCAL_PATH;
    private ApiPreload mApi;
    private Context mContext;

    public MyAdapter(Context context){
        LOCAL_PATH = context.getExternalFilesDir("download").getAbsolutePath();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.1.1.86:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApi = retrofit.create(ApiPreload.class);
        mContext = context;
    }

    public static boolean isStrEmpty(String str) {
        if (str == null)
            return true;
        if ("".equals(str))
            return true;
        String regex = "^\\s+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    @Override
    public void onWindowCreate(Activity activity) {
    }

    @Override
    public void onWindowResume(Activity activity) {

    }

    @Override
    public void onWindowPaused(Activity activity) {

    }

    @Override
    public void onWindowClosed(Activity activity) {
    }

    @Override
    public void onWindowResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean showImage(String url) {
        Intent intent = new Intent(mContext, ImageActivity.class);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        return true;
    }

    @Override
    public boolean showAlert(String title, String message, String cancel, String[] btns, AlertCallback cb) {
        return false;
    }

    @Override
    public boolean doShare(int type, ShareCallback callback) {
        return false;
    }

    @Override
    public boolean getUserInfo(UserInfoCallback callback) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("name", "swallow");
            jo.put("token", "nDKisieAz78Pdcbt");
            callback.callback("'"+jo.toString()+"'");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean logout() {
        Toast.makeText(mContext, "退出登录", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean kickout(String message) {
        Toast.makeText(mContext, "您的帐号已在另一设备登录", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean request(String path, String paramJson, final RequestCallback cb) {
        mApi.flatCall(path).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String resJson = new String(response.body().bytes());
                    cb.callback("'"+resJson+"'");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
        return true;
    }

    @Override
    public boolean goHome() {
        BaseActivity activity = BaseActivity.getCurrentActivity();
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        return true;
    }

    @Override
    public boolean openMap(String address) {
        return false;
    }

    @Override
    public boolean updateProload(String feature, final PreloadUpdateCallback cb) {
        mApi.askUpdate("").enqueue(new Callback<ShouldUpdateBean>() {
            @Override
            public void onResponse(Call<ShouldUpdateBean> call, Response<ShouldUpdateBean> response) {
                if (response == null || response.body() == null)
                    return;
                //是否有更新
                final String feature = response.body().feature;
                final String url = response.body().url;
                if (isStrEmpty(feature) || isStrEmpty(url))
                    return;
                //下载更新
                mApi.download(url).enqueue(new DownloadCallback(LOCAL_PATH, feature, cb));
            }
            @Override
            public void onFailure(Call<ShouldUpdateBean> call, Throwable t) {
                Log.w("MyAdapter", "Preload ask update api failed!");
            }
        });
        return false;
    }

    @Override
    public boolean interceptUrl(String url, AfterHandle cb) {
        if (url.equals("http://not_support.function.native/")) {
            Toast.makeText(mContext, url, Toast.LENGTH_SHORT).show();
            cb.turnTo("http://www.baidu.com");
            return true;
        }
        return false;
    }

    //存储数据到文件并以指定名字命名
    private boolean storeFile(File dir, String name, byte[] data){
        FileOutputStream fos = null;
        try {
            File file = new File(dir, name);
            fos = new FileOutputStream(file);
            fos.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 下载接口回调
     * @author swallow
     * @since 2016.4.26
     */
    private class DownloadCallback implements Callback<ResponseBody> {
        private PreloadUpdateCallback callback;
        private File mStoreDir;
        private String mFeature;
        public DownloadCallback(File storeDir, String feature, PreloadUpdateCallback cb) {
            this.mStoreDir = storeDir;
            this.mFeature = feature;
            callback = cb;
        }
        public DownloadCallback(String storePath, String feature, PreloadUpdateCallback cb) {
            this(new File(storePath), feature, cb);
        }
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                byte[] data = response.body().bytes();
                if (storeFile(mStoreDir, mFeature + ".zip", data)) {
                    callback.callback(mFeature, new File(mStoreDir, mFeature+".zip").getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.w("MyAdapter", "Preload download api failed!");
        }
    }

    public interface ApiPreload {
        @FormUrlEncoded
        @POST("api")
        Call<ShouldUpdateBean> askUpdate(@Field("feature") String feature);
        @GET
        Call<ResponseBody> download(@Url String url);
        @POST
        Call<ResponseBody> flatCall(@Url String url);
    }
}
