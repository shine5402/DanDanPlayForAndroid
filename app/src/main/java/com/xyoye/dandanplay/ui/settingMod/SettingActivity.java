package com.xyoye.dandanplay.ui.settingMod;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.bugly.beta.Beta;
import com.xyoye.core.base.BaseActivity;
import com.xyoye.dandanplay.R;
import com.xyoye.dandanplay.mvp.impl.SettingPresenterImpl;
import com.xyoye.dandanplay.mvp.presenter.SettingPresenter;
import com.xyoye.dandanplay.mvp.view.SettingView;
import com.xyoye.dandanplay.ui.danmuMod.DownloadBilibiliActivity;
import com.xyoye.dandanplay.ui.fileManagerMod.FileManagerActivity;
import com.xyoye.dandanplay.utils.AppConfigShare;

import butterknife.BindView;

/**
 * Created by YE on 2018/7/24.
 */


public class SettingActivity extends BaseActivity<SettingPresenter> implements SettingView, View.OnClickListener{
    public final static int SELECT_SETTING_FOLDER = 105;

    @BindView(R.id.path_rl)
    ConstraintLayout pathRl;
    @BindView(R.id.download_rl)
    ConstraintLayout downloadRl;
    @BindView(R.id.version_rl)
    ConstraintLayout versionRl;
    @BindView(R.id.about_rl)
    ConstraintLayout aboutRl;
    @BindView(R.id.feedback_rl)
    ConstraintLayout feedbackRl;
    @BindView(R.id.version_tv)
    TextView versionTv;
    @BindView(R.id.download_path_tv)
    TextView pathTv;

    private String version;

    @Override
    public void initView() {
        setTitle("设置");

        String downloadPath = AppConfigShare.getInstance().getDownloadFolder();
        pathTv.setText(downloadPath);
        version = AppConfigShare.getLocalVersion(this);
        versionTv.setText(version);
    }

    @Override
    public void initListener() {
        pathRl.setOnClickListener(this);
        downloadRl.setOnClickListener(this);
        versionRl.setOnClickListener(this);
        aboutRl.setOnClickListener(this);
        feedbackRl.setOnClickListener(this);
    }

    @NonNull
    @Override
    protected SettingPresenter initPresenter() {
        return new SettingPresenterImpl(this, this);
    }

    @Override
    protected int initPageLayoutID() {
        return R.layout.activity_setting;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.path_rl:
                Intent intent = new Intent(SettingActivity.this, FileManagerActivity.class);
                intent.putExtra(FileManagerActivity.IS_FOLDER, true);
                startActivityForResult(intent, SELECT_SETTING_FOLDER);
                break;
            case R.id.download_rl:
                launchActivity(DownloadBilibiliActivity.class);
                break;
            case R.id.version_rl:
                Beta.checkUpgrade(false,false);
                break;
            case R.id.about_rl:
                launchActivity(AboutActivity.class);
                break;
            case R.id.feedback_rl:
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("选择反馈方式");
                //    指定下拉列表的显示数据
                final String[] ways = {"邮件", "Github Issue"};
                //    设置一个下拉的列表选择项
                builder.setItems(ways, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (ways[which].equals("邮件")) {
                            builder.show();
                            String android_version = "Android "+android.os.Build.VERSION.RELEASE;
                            String phone_version = android.os.Build.MODEL;
                            String app_version = getResources().getString(R.string.app_name)+" 版本"+version;

                            Intent mail_intent = new Intent(android.content.Intent.ACTION_SEND);
                            mail_intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"yeshao1997@outlook.com"});
                            mail_intent.putExtra(Intent.EXTRA_SUBJECT, "弹弹Play - 反馈");
                            mail_intent.putExtra(Intent.EXTRA_TEXT, phone_version+"\n"+android_version+"\n\n"+app_version);
                            mail_intent.setType("text/plain");
                            startActivity(Intent.createChooser(mail_intent, "选择邮件客户端"));
                        }
                        else if (ways[which].equals("Github Issue")){
                            Uri uri = Uri.parse("https://github.com/xyoye/DanDanPlayForAndroid/issues");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    }
                });
                builder.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == SELECT_SETTING_FOLDER){
                String folderPath = data.getStringExtra("folder");
                pathTv.setText(folderPath);
                AppConfigShare.getInstance().setDownloadFolder(folderPath);
            }
        }
    }
}
