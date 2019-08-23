package com.hxh.appupdater.updater.net;

import com.hxh.appupdater.ui.AppUpdateDialogFragment;

import java.io.File;

/**
 * Created by HXH at 2019/8/22
 * 网络请求接口
 */
public interface INetManger {
    // get请求
    void get(String url, INetCallBack callBack, Object tag);

    // 下载
    void download(String url, File targetFile, INetDownloadCallBack callBack, Object tag);

    // 取消
    void cancel(Object tag);
}
