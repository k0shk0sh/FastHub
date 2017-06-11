package com.fastaccess.ui.widgets;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.fastaccess.R;
import com.fastaccess.helper.PrefGetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import petrov.kristiyan.colorpicker.ColorPicker;

/**
 * Created by Hamad on 6/11/17.
 */

public class ColorPickerPreference extends Preference implements Preference.OnPreferenceClickListener, ColorPicker.OnChooseColorListener {

    private int mValue = Color.BLACK;
    private ColorPicker colorPicker;

    public ColorPickerPreference(Context context) {
        super(context);
        init(context, null);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOnPreferenceClickListener(this);
    }

    @Override
    protected void onClick() {
        super.onClick();

        /*
            colors should no be setup like this must be a better way to retrive them from the theme
            or from preferences
            also relaying on constant integers worries me
         */
        String[] colors = {"#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800","#FF5722"};
        colorPicker = new ColorPicker(getContext()).setRoundColorButton(true).setColors(new ArrayList<>(Arrays.asList(colors)));
        colorPicker.setDefaultColorButton(Color.parseColor(colors[PrefGetter.getThemeColor(getContext()) - 1]));
        colorPicker.setOnChooseColorListener(this);
        colorPicker.show();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override
    public void onChooseColor(int position, int color) {
        // put code
        //getOnPreferenceChangeListener().onPreferenceChange(ColorPickerPreference.this, color);
        Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
        persistString(getContext().getResources().getStringArray(R.array.theme_colors)[position]);
        getOnPreferenceChangeListener().onPreferenceChange(this, getContext().getResources().getStringArray(R.array.theme_colors)[position]);

    }

    @Override
    public void onCancel() {

    }
}
