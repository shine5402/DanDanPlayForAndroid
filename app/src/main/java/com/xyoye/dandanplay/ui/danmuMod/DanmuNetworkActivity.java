package com.xyoye.dandanplay.ui.danmuMod;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.xyoye.core.adapter.BaseRvAdapter;
import com.xyoye.core.base.BaseActivity;
import com.xyoye.core.interf.AdapterItem;
import com.xyoye.core.utils.StringUtils;
import com.xyoye.dandanplay.R;
import com.xyoye.dandanplay.bean.DanmuMatchBean;
import com.xyoye.dandanplay.bean.params.DanmuMatchParam;
import com.xyoye.dandanplay.event.DownloadDanmuEvent;
import com.xyoye.dandanplay.event.OpenDanmuFolderEvent;
import com.xyoye.dandanplay.mvp.impl.DanmuNetworkPresenterImpl;
import com.xyoye.dandanplay.mvp.presenter.DanmuNetworkPresenter;
import com.xyoye.dandanplay.mvp.view.DanmuNetworkView;
import com.xyoye.dandanplay.weight.decorator.SpacesItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

/**
 * Created by YE on 2018/7/4 0004.
 */


public class DanmuNetworkActivity extends BaseActivity<DanmuNetworkPresenter> implements DanmuNetworkView{
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private BaseRvAdapter<DanmuMatchBean.MatchesBean> adapter;


    @Override
    public void initView() {
        setTitle("选择网络弹幕");
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.addItemDecoration(new SpacesItemDecoration(0,0,0,1));
    }

    @Override
    public void initListener() {

    }

    @NonNull
    @Override
    protected DanmuNetworkPresenter initPresenter() {
        return new DanmuNetworkPresenterImpl(this, this);
    }

    @Override
    protected int initPageLayoutID() {
        return R.layout.activity_danmu_network;
    }

    @Override
    public String getVideoPath() {
        return getIntent().getStringExtra("path");
    }

    @Override
    public void refreshAdapter(List<DanmuMatchBean.MatchesBean> beans) {
        if (adapter == null){
            adapter = new BaseRvAdapter<DanmuMatchBean.MatchesBean>(beans) {
                @NonNull
                @Override
                public AdapterItem<DanmuMatchBean.MatchesBean> onCreateItem(int viewType) {
                    return new DanmuNetworkItem();
                }
            };
            recyclerView.setAdapter(adapter);
        }else {
            adapter.setData(beans);
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downloadDanmu(DownloadDanmuEvent event){
        DanmuMatchBean.MatchesBean bean = event.getModel();
        DanmuDownloadDialog dialog = new DanmuDownloadDialog(this, R.style.Dialog, bean);
        dialog.show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setDanmu(OpenDanmuFolderEvent event){
        String path = event.getPath();
        Intent intent = getIntent();
        intent.putExtra("path", path);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        dismissLoadingDialog();
    }

    @Override
    public void showError(String message) {
        ToastUtils.showShort(message);
    }
}
