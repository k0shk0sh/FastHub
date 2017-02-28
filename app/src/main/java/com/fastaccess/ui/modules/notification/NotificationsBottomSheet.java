package com.fastaccess.ui.modules.notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseBottomSheetDialog;

import butterknife.BindView;

/**
 * Created by Kosh on 20 Feb 2017, 8:58 PM
 */

public class NotificationsBottomSheet extends BaseBottomSheetDialog {

    @BindView(R.id.toolbar) Toolbar toolbar;

    public static NotificationsBottomSheet newInstance() {
        return new NotificationsBottomSheet();
    }

    @Override protected int layoutRes() {
        return R.layout.notifications_bottom_sheet_layout;
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_drop_down);
        toolbar.setNavigationContentDescription(getString(R.string.back));
        toolbar.setTitle(R.string.notifictions);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.notificationView, NotificationsView.newInstance())
                    .commit();
        }
    }
}
