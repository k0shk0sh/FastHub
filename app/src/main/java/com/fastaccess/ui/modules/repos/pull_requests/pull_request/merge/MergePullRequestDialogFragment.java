package com.fastaccess.ui.modules.repos.pull_requests.pull_request.merge;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 18 Mar 2017, 12:13 PM
 */

public class MergePullRequestDialogFragment extends BaseDialogFragment<MergePullReqeustMvp.View, MergePullRequestPresenter>
        implements MergePullReqeustMvp.View {

    @BindView(R.id.title) TextInputLayout title;

    private MergePullReqeustMvp.MergeCallback mergeCallback;

    public static MergePullRequestDialogFragment newInstance(@Nullable String title) {
        MergePullRequestDialogFragment view = new MergePullRequestDialogFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.EXTRA, title)
                .end());
        return view;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MergePullReqeustMvp.MergeCallback) {
            mergeCallback = (MergePullReqeustMvp.MergeCallback) context;
        } else if (getParentFragment() instanceof MergePullReqeustMvp.MergeCallback) {
            mergeCallback = (MergePullReqeustMvp.MergeCallback) getParentFragment();
        }
    }

    @Override public void onDetach() {
        mergeCallback = null;
        super.onDetach();
    }

    @Override protected int fragmentLayout() {
        return R.layout.merge_dialog_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            String titleMsg = getArguments().getString(BundleConstant.EXTRA);
            if (!InputHelper.isEmpty(titleMsg)) {
                if (title.getEditText() != null) title.getEditText().setText(titleMsg);
            }
        }
    }

    @NonNull @Override public MergePullRequestPresenter providePresenter() {
        return new MergePullRequestPresenter();
    }

    @OnClick({R.id.cancel, R.id.ok}) public void onClick(View view) {
        if (view.getId() == R.id.ok) {
            boolean isEmpty = InputHelper.isEmpty(title);
            title.setError(isEmpty ? getString(R.string.required_field) : null);
            if (isEmpty) return;
            mergeCallback.onMerge(InputHelper.toString(title));
        }
        dismiss();
    }
}
