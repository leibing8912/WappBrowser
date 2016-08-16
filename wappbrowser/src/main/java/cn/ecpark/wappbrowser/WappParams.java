package cn.ecpark.wappbrowser;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * @className: WappParams
 * @classDescription: 浏览器可配置选项
 * @author: swallow
 * @createTime: 2016/4/15
 */
public class WappParams {
    //Constants
    public static final int BROWSE_TYPE_EXTERN = 0;
    public static final int BROWSE_TYPE_WEBAPP = 1;
    public static final int BROWSE_TYPE_APP = 2;
    public static final int BROWSE_TYPE_NATIVE = 3;

    public static final String YM_DL_ACTION = "cn.ecparck.depplinking";

    //Params
    public int mType;
    public String mTitle;
    public String mUrl;

    private WappParams(Builder builder){
        this.mTitle = builder.title;
        this.mType = builder.type;
        this.mUrl = builder.url;
    }

    private WappParams(){}

    /**
     * 将一个BrowserParams对象转化为json表示
     * @author swallow
     * @createTime 2016/4/15
     * @lastModify 2016/4/15
     * @param
     * @return Json string
     */
    public String toJson() {
        JSONObject jo = new JSONObject();
        Field[] fields = getClass().getDeclaredFields();
        for (Field field:fields) {
            try {
                jo.put(field.getName(), field.get(this));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return jo.toString();
    }

    /**
     * 将一个Json字符串转化成一个BrowserParams对象
     * @author swallow
     * @createTime 2016/4/15
     * @lastModify 2016/4/15
     * @param strJson 要转化的Json字符串
     * @return
     */
    public static WappParams fromJson(String strJson) {
        WappParams params = new WappParams();
        Class bpCls = WappParams.class;
        try {
            JSONObject jo = new JSONObject(strJson);
            Iterator<String> keys = jo.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Field field = bpCls.getDeclaredField(key);
                field.setAccessible(true);
                field.set(params, jo.get(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * @className: Builder
     * @classDescription: 浏览器可配置选项建造器
     * @author: swallow
     * @createTime: 2016/4/15
     */
    public static class Builder {
        private int type = 0;
        private String title = "";
        private String url = "";

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public WappParams build() {
            return new WappParams(this);
        }
    }
}
