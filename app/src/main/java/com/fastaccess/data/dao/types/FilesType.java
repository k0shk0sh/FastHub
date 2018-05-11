package com.fastaccess.data.dao.types;

import android.support.annotation.DrawableRes;

import com.fastaccess.R;

/**
 * Created by Kosh on 17 Feb 2017, 7:45 PM
 */

public enum FilesType {
    file(R.drawable.ic_file_document),
    dir(R.drawable.ic_folder),
    blob(R.drawable.ic_file_document),
    tree(R.drawable.ic_folder),
    symlink(R.drawable.ic_submodule);

    int icon;

    FilesType(int icon) {
        this.icon = icon;
    }

    @DrawableRes public int getIcon() {
        return icon > 0 ? icon : R.drawable.ic_file_document;
    }
}
