package com.hxh.appupdater.updater.net.okttp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.hxh.appupdater.updater.net.INetCallBack;
import com.hxh.appupdater.updater.net.INetDownloadCallBack;
import com.hxh.appupdater.updater.net.INetManger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HXH at 2019/8/22
 * OkHttp网络请求实现
 */
public class OkHttpNetManger implements INetManger {

    private static OkHttpClient sOkHttpClient;
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    static {
        sOkHttpClient = new OkHttpClient.Builder()
                .callTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void get(String url, INetCallBack callBack, Object tag) {
        Request request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            sHandler.post(() -> callBack.failed(e));
            return;
        }
        sOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("hxh", "onFailure get thread: " + Thread.currentThread().getName());
                if (call.isCanceled()) {
                    return;
                }
                sHandler.post(() -> callBack.failed(e));
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.i("hxh", "onResponse get thread: " + Thread.currentThread().getName());
                if (call.isCanceled()) {
                    return;
                }
                String s;
                try {
                    s = response.body().string();
                    if (call.isCanceled()) {
                        return;
                    }
                    String finalS = s;
                    sHandler.post(() -> callBack.success(finalS));
                } catch (Exception e) {
                    if (call.isCanceled()) {
                        return;
                    }
                    e.printStackTrace();
                    sHandler.post(() -> callBack.failed(e));
                }
            }
        });
    }

    @Override
    public void download(String url, File targetFile, INetDownloadCallBack callBack, Object tag) {
        Request request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .tag(tag)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            sHandler.post(() -> callBack.failed(e));
            return;
        }
        sOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("hxh", "onFailure download thread: " + Thread.currentThread().getName());
                if (call.isCanceled()) {
                    return;
                }
                sHandler.post(() -> callBack.failed(e));
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.i("hxh", "onResponse download thread: " + Thread.currentThread().getName());
                InputStream is = null;
                OutputStream os = null;
                try {
                    long total = Objects.requireNonNull(response.body()).contentLength();
                    is = response.body().byteStream();
                    os = new FileOutputStream(targetFile);
                    byte[] buffer = new byte[8 * 1024];
                    int len;
                    long read = 0;
                    int progress = -1;
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                        os.flush();
                        read += len;
                        int p = (int) ((read * 1f) / total * 100);
                        if (call.isCanceled()) {
                            return;
                        }
                        if (p != progress) {
                            progress = p;
                            sHandler.post(() -> callBack.progress(p));
                        }
                    }
                } catch (Exception e) {
                    if (call.isCanceled()) {
                        return;
                    }
                    e.printStackTrace();
                    sHandler.post(() -> callBack.failed(e));
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (call.isCanceled()) {
                    return;
                }
                sHandler.post(callBack::success);
            }
        });
    }

    @Override
    public void cancel(Object tag) {
        List<Call> queuedCalls = sOkHttpClient.dispatcher().queuedCalls();
        for (Call c : queuedCalls) {
            if (tag.equals(c.request().tag())) {
                c.cancel();
            }
        }
        List<Call> runningCalls = sOkHttpClient.dispatcher().runningCalls();
        for (Call c : runningCalls) {
            if (tag.equals(c.request().tag())) {
                c.cancel();
            }
        }
    }
}
