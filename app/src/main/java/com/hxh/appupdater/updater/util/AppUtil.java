package com.hxh.appupdater.updater.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.hxh.appupdater.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by HXH at 2019/8/22
 * app工具类
 */
public class AppUtil {

    // 版本号
    public static int getAppVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    // MD5
    @NonNull
    public static String getMD5(File file) {
        FileInputStream fis = null;
        try {
            BigInteger bi;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8 * 1024];
            fis = new FileInputStream(file);
            int len;
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            byte[] bytes = md.digest();
            bi = new BigInteger(1, bytes);
            return bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    // 安装apk
    public static void installApk(Context context, File apkFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        // Android N 文件URI适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
