package com.fastaccess.ui.modules.repos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;

/**
 * Created by Kosh on 11 Mar 2017, 12:10 AM
 */

public class DummyFragment extends Fragment {

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.single_container_layout, container, false);
    }
}
