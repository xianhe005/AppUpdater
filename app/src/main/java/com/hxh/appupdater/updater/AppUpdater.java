package com.hxh.appupdater.updater;

import com.hxh.appupdater.updater.net.INetManger;

/**
 * Created by HXH at 2019/8/22
 * 应用内更新器
 */
public class AppUpdater {
    private static AppUpdater sManager = new AppUpdater();

    private AppUpdater() {
    }

    private INetManger mINetManger;

    public void setINetManger(INetManger INetManger) {
        mINetManger = INetManger;
    }

    public static AppUpdater getInstance() {
        return sManager;
    }

    public INetManger getINetManger() {
        return mINetManger;
    }


}
