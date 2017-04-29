package com.fastaccess.ui.modules.repos.extras.labels.create;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

/**
 * Created by Kosh on 02 Apr 2017, 5:30 PM
 */

public interface CreateLabelMvp {

    interface View extends BaseMvp.FAView {
        void onSuccessfullyCreated(@NonNull LabelModel labelModel1);

        void onColorSelected(@NonNull String color);
    }

    interface Presenter extends BaseViewHolder.OnItemClickListener<String> {
        void onSubmitLabel(@NonNull String name, @NonNull String color,
                           @NonNull String repo, @NonNull String login);
    }
}
