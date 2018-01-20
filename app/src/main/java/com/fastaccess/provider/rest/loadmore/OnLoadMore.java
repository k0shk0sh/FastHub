package com.fastaccess.provider.rest.loadmore;

import android.support.annotation.Nullable;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.scroll.InfiniteScroll;

public class OnLoadMore<P> extends InfiniteScroll {

    private BaseMvp.PaginationListener<P> presenter;
    @Nullable private P parameter;

    public OnLoadMore(BaseMvp.PaginationListener<P> presenter) {
        this(presenter, null);
    }

    public OnLoadMore(BaseMvp.PaginationListener<P> presenter, @Nullable P parameter) {
        super();
        this.presenter = presenter;
        this.parameter = parameter;
    }


    public void setParameter(@Nullable P parameter) {
        this.parameter = parameter;
    }

    @Nullable public P getParameter() {
        return parameter;
    }

    @Override public boolean onLoadMore(int page, int totalItemsCount) {
        if (presenter != null) {
            presenter.setPreviousTotal(totalItemsCount);
            return presenter.onCallApi(page + 1, parameter);
        }
        return false;
    }
}