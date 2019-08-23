package com.hxh.appupdater.updater.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by HXH at 2019/8/22
 * 更新实体<br/>
 * {
 * "title":"4.5.0更新啦！",
 * "content":"1. 优化了阅读体验；\n2. 上线了 hyman 的课程；\n3. 修复了一些已知问题。",
 * "url":"http://59.110.162.30/v450_imooc_updater.apk",
 * "md5":"14480fc08932105d55b9217c6d2fb90b",
 * "versionCode":"450"
 * }
 */
public class UpdateBean implements Serializable {

    public String title;
    public String content;
    public String url;
    public String md5;
    public String versionCode;

    public static UpdateBean toBean(String json) {
        try {
            JSONObject jb = new JSONObject(json);
            UpdateBean bean = new UpdateBean();
            bean.title = jb.getString("title");
            bean.content = jb.getString("content");
            bean.url = jb.getString("url");
            bean.md5 = jb.getString("md5");
            bean.versionCode = jb.getString("versionCode");
            return bean;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
