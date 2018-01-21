package com.fastaccess.ui.modules.gists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.gists.create.CreateGistActivity;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.widgets.ViewPagerView;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 25 Mar 2017, 11:28 PM
 */

public class GistsListActivity extends BaseActivity {

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.gistsContainer) ViewPagerView pager;
    @BindView(R.id.fab) FloatingActionButton fab;

    public static void startActivity(@NonNull Context context) {
        context.startActivity(new Intent(context, GistsListActivity.class));
    }

    @Override protected int layout() {
        return R.layout.gists_activity_layout;
    }

    @Override protected boolean isTransparent() {
        return true;
    }

    @Override protected boolean canBack() {
        return true;
    }

    @Override protected boolean isSecured() {
        return false;
    }

    @NonNull @Override public TiPresenter providePresenter() {
        return new BasePresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.gists);
        setTaskName(getString(R.string.gists));
        setupTabs();
        fab.show();
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager) {
            @Override public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                onScrollTop(tab.getPosition());
            }
        });
    }

    @Override public void onScrollTop(int index) {
        if (pager == null || pager.getAdapter() == null) return;
        Fragment fragment = (BaseFragment) pager.getAdapter().instantiateItem(pager, index);
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onScrollTop(index);
        }
    }

    @OnClick(R.id.fab) public void onViewClicked() {
        ActivityHelper.startReveal(this, new Intent(this, CreateGistActivity.class), fab, BundleConstant.REQUEST_CODE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == BundleConstant.REQUEST_CODE) {
            if (pager != null && pager.getAdapter() != null) {
                ((Fragment) pager.getAdapter().instantiateItem(pager, 0)).onActivityResult(resultCode, resultCode, data);
            }
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupTabs() {
        pager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapterModel.buildForGists(this)));
        tabs.setupWithViewPager(pager);
    }
}
