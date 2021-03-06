package com.xyoye.dandanplay.ui.personalMod;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xyoye.core.adapter.BaseRvAdapter;
import com.xyoye.core.base.BaseActivity;
import com.xyoye.core.interf.AdapterItem;
import com.xyoye.dandanplay.R;
import com.xyoye.dandanplay.bean.AnimeFavoriteBean;
import com.xyoye.dandanplay.event.OpenAnimaDetailEvent;
import com.xyoye.dandanplay.mvp.impl.PersonalPresenterImpl;
import com.xyoye.dandanplay.mvp.presenter.PersonalFavoritePresenter;
import com.xyoye.dandanplay.mvp.view.PeronalFavoriteView;
import com.xyoye.dandanplay.ui.animeMod.AnimeDetailActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * Created by YE on 2018/7/24.
 */


public class PersonalFavoriteActivity extends BaseActivity<PersonalFavoritePresenter> implements PeronalFavoriteView {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private BaseRvAdapter<AnimeFavoriteBean.FavoritesBean> adapter;

    @Override
    public void initView() {
        setTitle("我的关注");

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        AnimeFavoriteBean favoriteBean = (AnimeFavoriteBean)getIntent().getSerializableExtra("bean");
        if (favoriteBean != null){
            adapter = new BaseRvAdapter<AnimeFavoriteBean.FavoritesBean>(favoriteBean.getFavorites()) {
                @NonNull
                @Override
                public AdapterItem<AnimeFavoriteBean.FavoritesBean> onCreateItem(int viewType) {
                    return new PersonalFavoriteAnimaItem();
                }
            };
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void initListener() {

    }

    @NonNull
    @Override
    protected PersonalFavoritePresenter initPresenter() {
        return new PersonalPresenterImpl(this, this);
    }

    @Override
    protected int initPageLayoutID() {
        return R.layout.activity_personal_favorite;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openAnimaDeatil(OpenAnimaDetailEvent event){
        Intent intent = new Intent(this, AnimeDetailActivity.class);
        intent.putExtra("animaId", event.getAnimaId());
        startActivity(intent);
    }
}
