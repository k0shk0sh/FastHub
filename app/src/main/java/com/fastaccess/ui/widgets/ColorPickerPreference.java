package com.fastaccess.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import com.fastaccess.R;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;

import java.util.ArrayList;
import java.util.HashMap;

import petrov.kristiyan.colorpicker.ColorPicker;

/**
 * Created by Hamad on 6/11/17.
 */

public class ColorPickerPreference extends Preference implements ColorPicker.OnChooseColorListener {

    public ColorPickerPreference(Context context) {
        super(context);
        init();
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setWidgetLayoutResource(R.layout.preference_widget_color);
    }

    @Override
    protected void onClick() {
        super.onClick();

        int selected_color = getSelectedColor();
        String title = String.format("Accent Color: (Currently: %s)", getSelectedColorName());
        ColorPicker colorPicker = new ColorPicker(getContext());
        colorPicker.setRoundColorButton(true);
        colorPicker.setColors(R.array.theme_colors_hex);
        colorPicker.setDefaultColorButton(selected_color);
        colorPicker.setTitle(title);
        TextView title_tv = colorPicker.getDialogViewLayout().findViewById(R.id.title);
        title_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        colorPicker.getPositiveButton().setTextColor(ViewHelper.getPrimaryTextColor(getContext()));
        colorPicker.getNegativeButton().setTextColor(ViewHelper.getPrimaryTextColor(getContext()));
        colorPicker.setOnChooseColorListener(this);
        colorPicker.show();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final Button colorButton = (Button) holder.findViewById(R.id.color);
        colorButton.setBackgroundResource(R.drawable.circle_shape);
        colorButton.getBackground().setColorFilter(getSelectedColor(), PorterDuff.Mode.SRC_IN);
    }

    private int getSelectedColor() {
        TypedArray colorTypedArray = getContext().getResources().obtainTypedArray(R.array.theme_colors_hex);
        String[] colorNames = getContext().getResources().getStringArray(R.array.theme_colors);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int i = 0; i < colorTypedArray.length(); i++) {
            colors.add(colorTypedArray.getColor(i, 0));
        }
        colorTypedArray.recycle();
        HashMap<Integer, Integer> preferenceValueToColor = new HashMap<>();

        for(int i=0; i<colorNames.length; i++){
            preferenceValueToColor.put(PrefGetter.getThemeColor(getContext().getResources(), colorNames[i]), colors.get(i));
        }
        return preferenceValueToColor.get(PrefGetter.getThemeColor(getContext()));
    }

    @Override
    public void onChooseColor(int position, int color) {
        // put code
        //getOnPreferenceChangeListener().onPreferenceChange(ColorPickerPreference.this, color);
        persistString(getContext().getResources().getStringArray(R.array.theme_colors)[position]);
        getOnPreferenceChangeListener().onPreferenceChange(this, getContext().getResources().getStringArray(R.array.theme_colors)[position]);
    }

    @Override
    public void onCancel() {
        // do nothing when the dialog is canceled
    }

    private String getSelectedColorName() {
        String[] colorNames = getContext().getResources().getStringArray(R.array.theme_colors);
        return colorNames[PrefGetter.getThemeColor(getContext()) - 1];
    }
}
