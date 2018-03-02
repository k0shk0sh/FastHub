package com.fastaccess.ui.modules.repos.code.releases;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.SimpleUrlsModel;
import com.fastaccess.data.dao.model.Release;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.ReleasesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.ListDialogView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */

public class RepoReleasesFragment extends BaseFragment<RepoReleasesMvp.View, RepoReleasesPresenter> implements RepoReleasesMvp.View {
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private OnLoadMore onLoadMore;
    private ReleasesAdapter adapter;

    public static RepoReleasesFragment newInstance(@NonNull String repoId, @NonNull String login) {
        RepoReleasesFragment view = new RepoReleasesFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .end());
        return view;
    }

    public static RepoReleasesFragment newInstance(@NonNull String repoId, @NonNull String login, @Nullable String tag, long id) {
        RepoReleasesFragment view = new RepoReleasesFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, id)
                .put(BundleConstant.EXTRA_THREE, tag)
                .end());
        return view;
    }

    @Override public void onNotifyAdapter(@Nullable List<Release> items, int page) {
        hideProgress();
        if (items == null || items.isEmpty()) {
            adapter.clear();
            return;
        }
        if (page <= 1) {
            adapter.insertItems(items);
        } else {
            adapter.addItems(items);
        }
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new NullPointerException("Bundle is null, therefore, issues can't be proceeded.");
        }
        stateLayout.setEmptyText(R.string.no_releases);
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        recycler.addDivider();
        adapter = new ReleasesAdapter(getPresenter().getReleases());
        adapter.setListener(getPresenter());
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        } else if (getPresenter().getReleases().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
        fastScroller.attachRecyclerView(recycler);
    }

    @NonNull @Override public RepoReleasesPresenter providePresenter() {
        return new RepoReleasesPresenter();
    }

    @SuppressWarnings("unchecked") @NonNull @Override public OnLoadMore getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore(getPresenter());
        }
        return onLoadMore;
    }

    @Override public void showProgress(@StringRes int resId) {

        refresh.setRefreshing(true);

        stateLayout.showProgress();
    }

    @Override public void hideProgress() {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
    }

    @Override public void showErrorMessage(@NonNull String message) {
        showReload();
        super.showErrorMessage(message);
    }

    @Override public void showMessage(int titleRes, int msgRes) {
        showReload();
        super.showMessage(titleRes, msgRes);
    }

    @Override public void onDownload(@NonNull Release item) {
        ArrayList<SimpleUrlsModel> models = new ArrayList<>();
        if (!InputHelper.isEmpty(item.getZipBallUrl())) {
            String url = item.getZipBallUrl();
            if (!url.endsWith(".tar.gz")) {
                url = url + ".tar.gz";
            }
            models.add(new SimpleUrlsModel(getString(R.string.download_as_zip), url));
        }
        if (!InputHelper.isEmpty(item.getTarballUrl())) {
            models.add(new SimpleUrlsModel(getString(R.string.download_as_tar), item.getTarballUrl()));
        }
        if (item.getAssets() != null && !item.getAssets().isEmpty()) {
            ArrayList<SimpleUrlsModel> mapped = Stream.of(item.getAssets())
                    .filter(value -> value != null && value.getBrowserDownloadUrl() != null)
                    .map(assetsModel -> new SimpleUrlsModel(String.format("%s (%s)", assetsModel.getName(), assetsModel.getDownloadCount()),
                            assetsModel.getBrowserDownloadUrl()))
                    .collect(Collectors.toCollection(ArrayList::new));
            if (mapped != null && !mapped.isEmpty()) {
                models.addAll(mapped);
            }
        }
        ListDialogView<SimpleUrlsModel> dialogView = new ListDialogView<>();
        dialogView.initArguments(getString(R.string.releases), models);
        dialogView.show(getChildFragmentManager(), "ListDialogView");
    }

    @Override public void onShowDetails(@NonNull Release item) {
        if (!InputHelper.isEmpty(item.getBody())) {
            MessageDialogView.newInstance(!InputHelper.isEmpty(item.getName()) ? item.getName() : item.getTagName(),
                    item.getBody(), true, false).show(getChildFragmentManager(), MessageDialogView.TAG);
        } else {
            showErrorMessage(getString(R.string.no_body));
        }
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, null);
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    @Override public void onItemSelected(SimpleUrlsModel item) {
        if (ActivityHelper.checkAndRequestReadWritePermission(getActivity())) {
            RestProvider.downloadFile(getContext(), item.getUrl());
        }
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
