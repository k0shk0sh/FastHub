package com.fastaccess.ui.modules.repos.extras.labels;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.ui.adapter.LabelsAdapter;
import com.fastaccess.ui.base.mvp.BaseMvp;

import java.util.ArrayList;

/**
 * Created by Kosh on 22 Feb 2017, 7:22 PM
 */

public interface LabelsMvp {

    interface SelectedLabelsListener {
        void onSelectedLabels(@NonNull ArrayList<LabelModel> labels);
    }

    interface View extends BaseMvp.FAView, LabelsAdapter.OnSelectLabel {}

    interface Presenter {}
}
