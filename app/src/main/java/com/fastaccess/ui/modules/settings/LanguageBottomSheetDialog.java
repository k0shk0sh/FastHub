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

import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.AppLanguageModel;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.ui.base.BaseBottomSheetDialog;
import com.fastaccess.ui.widgets.FontTextView;

import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Action;

/**
 * Created by JediB on 5/12/2017.
 */

public class LanguageBottomSheetDialog extends BaseBottomSheetDialog {
    public interface LanguageDialogListener {
        void onLanguageChanged(Action action);
    }

    public static final String TAG = LanguageBottomSheetDialog.class.getSimpleName();

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.picker) RadioGroup radioGroup;
    private LanguageDialogListener listener;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LanguageDialogListener) {
            listener = (LanguageDialogListener) context;
        }
    }

    @Override public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override protected int layoutRes() {
        return R.layout.picker_dialog;
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String language = PrefGetter.getAppLanguage();
        String[] values = getResources().getStringArray(R.array.languages_array_values);
        List<AppLanguageModel> languageModels = Stream.of(getResources().getStringArray(R.array.languages_array))
                .mapIndexed((index, s) -> new AppLanguageModel(values[index], s))
                .sortBy(AppLanguageModel::getLabel)
                .toList();
        int padding = getResources().getDimensionPixelSize(R.dimen.spacing_xs_large);
        for (int i = 0; i < languageModels.size(); i++) {
            RadioButton radioButtonView = new RadioButton(getContext());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            radioButtonView.setLayoutParams(params);
            AppLanguageModel model = languageModels.get(i);
            radioButtonView.setText(model.getLabel());
            radioButtonView.setId(i);
            radioButtonView.setTag(model.getValue());
            radioButtonView.setGravity(Gravity.CENTER_VERTICAL);
            radioButtonView.setPadding(padding, padding, padding, padding);
            radioGroup.addView(radioButtonView);
            if (model.getValue().equalsIgnoreCase(language)) radioGroup.check(i);
        }
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String tag = (String) radioGroup.getChildAt(checkedId).getTag();
            Logger.e(tag);
            if (!tag.equalsIgnoreCase(language)) {
                PrefGetter.setAppLangauge(tag);
                if (listener != null) listener.onLanguageChanged(this::dismiss);
            }
        });

    }
}
