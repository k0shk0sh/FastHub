package com.fastaccess.ui.modules.repos.extras.milestone.create;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.datetimepicker.DatePickerFragmentDialog;
import com.fastaccess.datetimepicker.callback.DatePickerCallback;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnTouch;

/**
 * Created by Kosh on 04 Mar 2017, 10:40 PM
 */

public class CreateMilestoneDialogFragment extends BaseDialogFragment<CreateMilestoneMvp.View, CreateMilestonePresenter>
        implements CreateMilestoneMvp.View, DatePickerCallback {

    public static final String TAG = CreateMilestoneDialogFragment.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.title) TextInputLayout title;
    @BindView(R.id.dueOnEditText) TextInputEditText dueOnEditText;
    @BindView(R.id.description) TextInputLayout description;

    private CreateMilestoneMvp.OnMilestoneAdded onMilestoneAdded;

    public static CreateMilestoneDialogFragment newInstance(@NonNull String login, @NonNull String repo) {
        CreateMilestoneDialogFragment fragment = new CreateMilestoneDialogFragment();
        fragment.setArguments(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.ID, repo)
                .end());
        return fragment;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof CreateMilestoneMvp.OnMilestoneAdded) {
            onMilestoneAdded = (CreateMilestoneMvp.OnMilestoneAdded) getParentFragment();
        } else {
            onMilestoneAdded = (CreateMilestoneMvp.OnMilestoneAdded) context;
        }
    }

    @Override public void onDetach() {
        onMilestoneAdded = null;
        super.onDetach();
    }

    @Override protected int fragmentLayout() {
        return R.layout.create_milestone_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String login = getArguments().getString(BundleConstant.EXTRA);
        String repo = getArguments().getString(BundleConstant.ID);
        if (login == null || repo == null) {
            return;
        }
        toolbar.setTitle(R.string.create_milestone);
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setNavigationOnClickListener(item -> dismiss());
        toolbar.inflateMenu(R.menu.add_menu);
        toolbar.getMenu().findItem(R.id.add).setIcon(R.drawable.ic_send);
        toolbar.setOnMenuItemClickListener(item -> {
            getPresenter().onSubmit(InputHelper.toString(title), InputHelper.toString(dueOnEditText), InputHelper.toString(description), login, repo);
            return true;
        });
    }

    @NonNull @Override public CreateMilestonePresenter providePresenter() {
        return new CreateMilestonePresenter();
    }

    @OnTouch(R.id.dueOnEditText) boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            DatePickerFragmentDialog.newInstance().show(getChildFragmentManager(), "DatePickerFragmentDialog");
        }
        return false;
    }

    @Override public void onDateSet(long date) {
        if (date > 0) {
            dueOnEditText.setText(ParseDateFormat.prettifyDate(date));
        }
    }

    @Override public void onShowTitleError(boolean isError) {
        title.setError(isError ? getString(R.string.required_field) : null);
    }

    @Override public void onMilestoneAdded(@NonNull MilestoneModel milestoneModel) {
        hideProgress();
        onMilestoneAdded.onMilestoneAdded(milestoneModel);
        dismiss();
    }
}
