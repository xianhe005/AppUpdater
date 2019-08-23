package com.hxh.appupdater;

import android.app.Application;

import com.hxh.appupdater.updater.AppUpdater;
import com.hxh.appupdater.updater.net.okttp.OkHttpNetManger;

/**
 * Created by HXH at 2019/8/22
 * 应用上下文
 */
public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppUpdater.getInstance().setINetManger(new OkHttpNetManger());
    }
}
