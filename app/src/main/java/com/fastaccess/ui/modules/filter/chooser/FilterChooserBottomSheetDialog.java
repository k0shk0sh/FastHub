package com.fastaccess.ui.modules.filter.chooser;

import android.content.Context;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseBottomSheetDialog;

import butterknife.OnClick;

/**
 * Created by Kosh on 10 Apr 2017, 12:18 PM
 */

public class FilterChooserBottomSheetDialog extends BaseBottomSheetDialog {

    private FilterAddChooserListener listener;

    public static FilterChooserBottomSheetDialog newInstance() {
        return new FilterChooserBottomSheetDialog();
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof FilterAddChooserListener) {
            listener = (FilterAddChooserListener) getParentFragment();
        } else if (context instanceof FilterAddChooserListener) {
            listener = (FilterAddChooserListener) context;
        }
    }

    @Override public void onDestroy() {
        listener = null;
        super.onDestroy();
    }

    @Override protected int layoutRes() {
        return R.layout.add_filter_row_layout;
    }

    @OnClick({R.id.add, R.id.search}) public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add:
                listener.onAddSelected();
                break;
            case R.id.search:
                listener.onSearchSelected();
                break;
        }
        dismiss();
    }
}
