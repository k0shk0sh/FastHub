package com.fastaccess.ui.modules.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseBottomSheetDialog;

import butterknife.BindView;

/**
 * Created by Kosh on 02 Mar 2017, 7:51 PM
 */

public class SettingsBottomSheetDialog extends BaseBottomSheetDialog {

    private static final String TAG = SettingsBottomSheetDialog.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;

    public static void show(@NonNull FragmentManager fragmentManager) {
        new SettingsBottomSheetDialog().show(fragmentManager, SettingsBottomSheetDialog.TAG);
    }

    @Override protected int layoutRes() {
        return R.layout.settings_layout;
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setTitle(R.string.settings);
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settingsContainer, new SettingsFragment())
                    .commit();
        }
    }
}
