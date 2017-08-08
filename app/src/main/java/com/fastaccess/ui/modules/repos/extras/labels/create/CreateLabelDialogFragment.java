package com.fastaccess.ui.modules.repos.extras.labels.create;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.adapter.LabelColorsAdapter;
import com.fastaccess.ui.base.BaseDialogFragment;
import com.fastaccess.ui.modules.repos.extras.labels.LabelsMvp;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.Arrays;

import butterknife.BindView;

/**
 * Created by Kosh on 02 Apr 2017, 5:38 PM
 */

public class CreateLabelDialogFragment extends BaseDialogFragment<CreateLabelMvp.View, CreateLabelPresenter> implements CreateLabelMvp.View {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.name) TextInputLayout name;
    @BindView(R.id.description) TextInputLayout description;
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private LabelsMvp.View callback;

    public static CreateLabelDialogFragment newInstance(@NonNull String login, @NonNull String repo) {
        CreateLabelDialogFragment fragment = new CreateLabelDialogFragment();
        fragment.setArguments(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repo)
                .end());
        return fragment;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof LabelsMvp.View) {
            callback = (LabelsMvp.View) getParentFragment();
        } else if (context instanceof LabelsMvp.View) {
            callback = (LabelsMvp.View) context;
        }
    }

    @Override public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override public void onSuccessfullyCreated(@NonNull LabelModel labelModel1) {
        hideProgress();
        if (callback != null) callback.onLabelAdded(labelModel1);
        dismiss();
    }

    @Override public void onColorSelected(@NonNull String color) {
        description.getEditText().setText(color.replaceFirst("#", ""));
    }

    @Override protected int fragmentLayout() {
        return R.layout.create_label_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String login = getArguments().getString(BundleConstant.EXTRA);
        String repo = getArguments().getString(BundleConstant.ID);
        if (login == null || repo == null) {
            return;
        }
        recycler.setAdapter(new LabelColorsAdapter(Arrays.asList(getResources().getStringArray(R.array.label_colors)), getPresenter()));
        recycler.addKeyLineDivider();
        toolbar.setTitle(R.string.create_label);
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setNavigationOnClickListener(item -> dismiss());
        toolbar.inflateMenu(R.menu.add_menu);
        toolbar.getMenu().findItem(R.id.add).setIcon(R.drawable.ic_send);
        toolbar.setOnMenuItemClickListener(item -> {
            boolean emptyColor = InputHelper.isEmpty(description);
            boolean emptyName = InputHelper.isEmpty(name);
            description.setError(emptyColor ? getString(R.string.required_field) : null);
            name.setError(emptyName ? getString(R.string.required_field) : null);
            if (!emptyColor && !emptyName) {
                getPresenter().onSubmitLabel(InputHelper.toString(name), InputHelper.toString(description), repo, login);
            }
            return true;
        });
        fastScroller.attachRecyclerView(recycler);
    }

    @NonNull @Override public CreateLabelPresenter providePresenter() {
        return new CreateLabelPresenter();
    }

}
