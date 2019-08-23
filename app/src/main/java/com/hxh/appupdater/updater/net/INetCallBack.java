package com.hxh.appupdater.updater.net;

/**
 * Created by HXH at 2019/8/22
 * 网络请求接口回调
 */
public interface INetCallBack {
    void success(String s);

    void failed(Throwable t);
}
