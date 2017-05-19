package com.fastaccess.ui.modules.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fastaccess.R;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.ui.base.BaseBottomSheetDialog;
import com.fastaccess.ui.widgets.FontButton;
import com.fastaccess.ui.widgets.FontTextView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by JediB on 5/12/2017.
 */

public class LanguageBottomSheetDialog extends BaseBottomSheetDialog {
    public interface LanguageDialogListener {
        void onDismissed();
    }

    public static final String TAG = LanguageBottomSheetDialog.class.getSimpleName();

    private String names[];
    private String values[];

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.picker) RadioGroup radioGroup;
    @BindView(R.id.cancel) FontButton cancel;
    @BindView(R.id.ok) FontButton ok;
    private SlackBottomSheetDialog.SlackDialogListener listener;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SlackBottomSheetDialog.SlackDialogListener) {
            listener = (SlackBottomSheetDialog.SlackDialogListener) context;
        }

        names = context.getResources().getStringArray(R.array.languages_array);
        values = context.getResources().getStringArray(R.array.languages_array_values);
    }

    @Override public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override protected int layoutRes() {
        return R.layout.picker_dialog;
    }

    @OnClick({R.id.cancel, R.id.ok}) public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                getActivity().setResult(RESULT_CANCELED);
        }
        if (listener != null) listener.onDismissed();
        dismiss();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int selected = Arrays.asList(values).indexOf(PrefHelper.getString("app_language"));
        String language = PrefHelper.getString("app_language");
        cancel.setText(R.string.cancel);
        ok.setText(R.string.ok);

        for (int i = 0; i < names.length; i++) {
            RadioButton radioButtonView = new RadioButton(getContext());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            radioButtonView.setLayoutParams(params);
            radioButtonView.setText(names[i]);
            radioButtonView.setId(i);
            radioButtonView.setGravity(Gravity.CENTER_VERTICAL);
            radioButtonView.setPadding((int) getResources().getDimension(R.dimen.spacing_xs_large), (int) getResources().getDimension(R.dimen
                            .spacing_xs_large),
                    (int) getResources().getDimension(R.dimen.spacing_xs_large), (int) getResources().getDimension(R.dimen.spacing_xs_large));
            radioGroup.addView(radioButtonView);
            if (i == selected)
                radioGroup.check(i);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int index = radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId()));

            PrefHelper.set("app_language", values[index]);
            if (language != values[index])
                getActivity().setResult(RESULT_OK);
        });

    }

    @Override protected void onHidden() {
        if (listener != null) listener.onDismissed();
        super.onHidden();
    }

    @Override protected void onDismissedByScrolling() {
        if (listener != null) listener.onDismissed();
        super.onDismissedByScrolling();
    }
}
