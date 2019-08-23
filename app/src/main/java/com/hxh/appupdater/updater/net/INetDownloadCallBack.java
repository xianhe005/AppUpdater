package com.hxh.appupdater.updater.net;

/**
 * Created by HXH at 2019/8/22
 * 网络更下载请求接口回调
 */
public interface INetDownloadCallBack {
    void success();

    void progress(int progress);

    void failed(Throwable t);
}
