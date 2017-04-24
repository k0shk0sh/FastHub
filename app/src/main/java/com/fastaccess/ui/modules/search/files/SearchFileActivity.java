package com.fastaccess.ui.modules.search.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.OnClick;

/**
 * Created by adibk on 4/23/17.
 */

public class SearchFileActivity extends BaseActivity {

    @Override
    protected int layout() {
        return R.layout.activity_search_file;
    }

    @Override
    protected boolean isTransparent() {
        return false;
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    protected boolean isSecured() {
        return false;
    }

    @NonNull
    @Override
    public TiPresenter providePresenter() {
        return new BasePresenter();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.back) public void onBackClicked() {
        onBackPressed();
    }
}
