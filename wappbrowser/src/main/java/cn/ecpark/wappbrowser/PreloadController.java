package cn.ecpark.wappbrowser;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 预加载控制器，用于预加载控制，比如检测更新，更新文件，校验文件等
 *
 * @author swallow
 * @since 2016.4.25
 */
class PreloadController {
    private static final String TAG = "PreloadController";
    public static String LOCAL_PATH;

    //Singleton
    private static PreloadController instance;

    public static PreloadController getInstance() {
        if (instance == null)
            instance = new PreloadController();
        return instance;
    }

    private PreloadController() {}

    /**
     * 初始化, 进行存储环境检查
     * @param context
     */
    public boolean init(Context context) {
        //外部存储器是否挂载
        String storageState = Environment.getExternalStorageState();
        if (!storageState.equals(Environment.MEDIA_MOUNTED))
            return false;
        //是否有外部存储器的读写权限
        boolean permission = (
                PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    context.getPackageName()
        ));
        if (!permission)
            return false;
        //初始化本地缓存目录
        LOCAL_PATH = context.getExternalFilesDir("preload").getAbsolutePath();
        File file = new File(LOCAL_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return true;
    }

    /**
     * 询问是否需要升级，如果需要升级则直接静默升级
     * @param adapter 用户适配器
     */
    public void askUpdate(WappAdapter adapter) {
        String feature = "";
        try {
            feature = getFeature(LOCAL_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            adapter.updateProload(feature, updateCb);
        }
    }

    /**
     * 更新回调
     */
    private WappAdapter.PreloadUpdateCallback updateCb = new WappAdapter.PreloadUpdateCallback() {
        @Override
        public void callback(String feature, String path) {
            File file = new File(path);
            if (!file.exists() || !file.isFile())
                return;

            //校验文件
            String dataFeature = getMd5(Utils.getFileBytes(path));
            if (dataFeature.equals(feature)) {
                //更新特征码
                try {
                    //解压文件
                    Utils.unZipFiles(file, LOCAL_PATH+"/");
                    writeFeature(LOCAL_PATH, dataFeature);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    file.delete();
                }
            }
        }
    };

    //获取特征码
    private String getFeature(String dir) throws IOException {
        String featureCode;
        File featureFile = new File(dir, "feature");
        if (!featureFile.exists()) {
            featureFile.createNewFile();
        }
        FileReader fr = new FileReader(featureFile);
        BufferedReader br = new BufferedReader(fr);
        featureCode = br.readLine();
        br.close();
        fr.close();
        return featureCode;
    }

    //写特征码
    private void writeFeature(String dir, String feature) throws IOException {
        File featureFile = new File(dir, "feature");
        if (!featureFile.exists()) {
            featureFile.createNewFile();
        }
        FileWriter fw = new FileWriter(featureFile);
        fw.write(feature);
        fw.close();
    }

    //获取byte数组的md5码
    private String getMd5(byte[] file) {
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(file, 0, file.length);
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
    }
}
