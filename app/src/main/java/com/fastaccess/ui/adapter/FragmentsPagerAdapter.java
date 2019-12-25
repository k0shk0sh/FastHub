package com.fastaccess.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.fastaccess.data.dao.FragmentPagerAdapterModel;

import java.util.List;

/**
 * Created by Kosh on 03 Dec 2016, 9:25 AM
 */

public class FragmentsPagerAdapter extends FragmentStatePagerAdapter {

    private List<FragmentPagerAdapterModel> fragments;

    public FragmentsPagerAdapter(FragmentManager fm, List<FragmentPagerAdapterModel> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override public Fragment getItem(int position) {
        return fragments.get(position).getFragment();
    }

    @Override public int getCount() {
        return fragments.size();
    }

    @Override public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }

    @Override public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }

    public void remove(FragmentPagerAdapterModel model) {
        if (fragments != null) {
            fragments.remove(model);
            notifyDataSetChanged();
        }
    }

    public void remove(int position) {
        if (fragments != null) {
            fragments.remove(position);
            notifyDataSetChanged();
        }
    }
}
