package com.fastaccess.ui.modules.repos.extras.labels;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.LabelsAdapter;
import com.fastaccess.ui.base.mvp.BaseMvp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 22 Feb 2017, 7:22 PM
 */

public interface LabelsMvp {

    interface SelectedLabelsListener {
        void onSelectedLabels(@NonNull ArrayList<LabelModel> labels);
    }

    interface View extends BaseMvp.FAView, LabelsAdapter.OnSelectLabel {

        @NonNull OnLoadMore getLoadMore();

        void onNotifyAdapter(@Nullable List<LabelModel> items, int page);

        void onLabelAdded(@NonNull LabelModel labelModel);
    }

    interface Presenter extends BaseMvp.PaginationListener {

        @NonNull ArrayList<LabelModel> getLabels();

    }
}
