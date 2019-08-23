package com.hxh.appupdater;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.hxh.appupdater.ui.AppUpdateDialogFragment;
import com.hxh.appupdater.updater.AppUpdater;
import com.hxh.appupdater.updater.bean.UpdateBean;
import com.hxh.appupdater.updater.net.INetCallBack;
import com.hxh.appupdater.updater.util.AppUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "hxh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(v -> {
            btnUpdate.setEnabled(false);
            //检测版本
            AppUpdater.getInstance().getINetManger().get("http://59.110.162.30/app_updater_version.json", new INetCallBack() {
                @Override
                public void success(String s) {
                    btnUpdate.setEnabled(true);
                    Log.i(TAG, "success: " + s);
                    // 1.解析数据
                    UpdateBean bean = UpdateBean.toBean(s);
                    if (bean == null) {
                        Toast.makeText(MainActivity.this, "版本更新数据解析失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 2.比对版本
                    try {
                        if (AppUtil.getAppVersionCode() >= Integer.valueOf(bean.versionCode)) {
                            return;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "版本更新版本号异常", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 如果需要更新
                    // 3.弹框
                    AppUpdateDialogFragment.show(MainActivity.this, bean);
                }

                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(MainActivity.this, "版本更新检查失败", Toast.LENGTH_SHORT).show();
                    btnUpdate.setEnabled(true);
                }
            }, MainActivity.this);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUpdater.getInstance().getINetManger().cancel(this);
    }
}
