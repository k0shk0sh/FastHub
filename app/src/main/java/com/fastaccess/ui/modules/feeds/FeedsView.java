package com.fastaccess.ui.modules.feeds;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.SimpleUrlsModel;
import com.fastaccess.helper.Logger;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.adapter.FeedsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.ListDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

public class FeedsView extends BaseFragment<FeedsMvp.View, FeedsPresenter> implements FeedsMvp.View {

    public static final String TAG = FeedsView.class.getSimpleName();

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private FeedsAdapter adapter;
    private OnLoadMore onLoadMore;

    public static FeedsView newInstance() {
        return new FeedsView();
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new FeedsAdapter(getPresenter().getEvents());
        adapter.setListener(getPresenter());
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        if (getPresenter().getEvents().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1);
    }

    @Override public void onNotifyAdapter() {
        hideProgress();
        adapter.notifyDataSetChanged();
    }

    @Override public void showProgress(@StringRes int resId) {
        refresh.setRefreshing(true);
        stateLayout.showProgress();
    }

    @Override public void hideProgress() {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        stateLayout.showReload(adapter.getItemCount());
        super.showErrorMessage(msgRes);
    }

    @Override public void onOpenRepoChooser(@NonNull ArrayList<SimpleUrlsModel> models) {
        Logger.e(models);
        ListDialogView<SimpleUrlsModel> dialogView = new ListDialogView<>();
        dialogView.initArguments(getString(R.string.repo_chooser), models);
        dialogView.show(getChildFragmentManager(), "ListDialogView");
    }

    @NonNull @Override public FeedsPresenter providePresenter() {
        return new FeedsPresenter();
    }

    @SuppressWarnings("unchecked") @NonNull @Override public OnLoadMore getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore(getPresenter());
        }
        return onLoadMore;
    }

    @Override public void onDestroyView() {
        recycler.removeOnScrollListener(getLoadMore());
        super.onDestroyView();
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    @Override public void onItemSelected(SimpleUrlsModel item) {
        SchemeParser.launchUri(getContext(), Uri.parse(item.getItem()));
    }
}
