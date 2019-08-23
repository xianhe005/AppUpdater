package com.hxh.appupdater.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hxh.appupdater.R;
import com.hxh.appupdater.updater.AppUpdater;
import com.hxh.appupdater.updater.bean.UpdateBean;
import com.hxh.appupdater.updater.net.INetDownloadCallBack;
import com.hxh.appupdater.updater.util.AppUtil;

import java.io.File;
import java.util.Objects;

/**
 * Created by HXH at 2019/8/22
 * 版本更新框
 */
public class AppUpdateDialogFragment extends DialogFragment {

    public static final String KEY_UPDATE_BEAN = "UPDATE_BEAN";
    private UpdateBean mUpdateBean;

    public static void show(FragmentActivity activity, UpdateBean bean) {
        AppUpdateDialogFragment df = new AppUpdateDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_UPDATE_BEAN, bean);
        df.setArguments(bundle);
        df.show(activity.getSupportFragmentManager(), "showUpdateDialog");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUpdateBean = (UpdateBean) getArguments().getSerializable(KEY_UPDATE_BEAN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update, container, false);
        bindViews(view);
        return view;
    }

    private void bindViews(View view) {
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvContent = view.findViewById(R.id.tv_content);
        TextView tvUpdate = view.findViewById(R.id.tv_update);
        tvTitle.setText(mUpdateBean.title);
        tvContent.setText(mUpdateBean.content);
        tvUpdate.setOnClickListener(v -> {
            // 4.下载更新
            File targetFile = new File(Objects.requireNonNull(getActivity()).getCacheDir(), "test.apk");
            if (targetFile.exists() && AppUtil.getMD5(targetFile).equals(mUpdateBean.md5)) {
                // 安装
                AppUtil.installApk(getActivity(), targetFile);
                return;
            }
            if (targetFile.getParentFile() != null) {
                targetFile.getParentFile().mkdirs();
            }
            tvUpdate.setEnabled(false);
            AppUpdater.getInstance().getINetManger().download(mUpdateBean.url, targetFile, new INetDownloadCallBack() {
                @Override
                public void success() {
                    //下载成功
                    // md5校验
                    if (!AppUtil.getMD5(targetFile).equals(mUpdateBean.md5)) {
                        Toast.makeText(getActivity(), "文件MD5校验失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tvUpdate.setEnabled(true);
                    tvUpdate.setText("下载更新");
                    dismiss();
                    // 安装
                    AppUtil.installApk(getActivity(), targetFile);
                }

                @Override
                public void progress(int progress) {
                    // 更新进度
                    tvUpdate.setText(String.format("%s%%", progress));
                    Log.i("hxh", "progress: " + progress);
                }

                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                    //下载失败
                    Toast.makeText(getActivity(), "下载apk失败", Toast.LENGTH_SHORT).show();
                    tvUpdate.setEnabled(true);
                    tvUpdate.setText("下载更新");
                }
            }, AppUpdateDialogFragment.this);
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        AppUpdater.getInstance().getINetManger().cancel(this);
    }
}
