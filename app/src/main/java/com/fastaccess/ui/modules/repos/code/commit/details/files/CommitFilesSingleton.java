package com.fastaccess.ui.modules.repos.code.commit.details.files;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.CommitFileListModel;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Kosh on 27 Mar 2017, 7:28 PM
 * Commits files could be so freaking large, so having this will avoid transactionToLargeException.
 */

class CommitFilesSingleton {
    private static final CommitFilesSingleton SINGLETON = new CommitFilesSingleton();

    static CommitFilesSingleton getInstance() {
        return SINGLETON;
    }

    private Map<String, CommitFileListModel> files = new Hashtable<>();

    private CommitFilesSingleton() {}

    void putFiles(@NonNull String id, @NonNull CommitFileListModel commitFiles) {
        clear();
        files.put(id, commitFiles);
    }

    @Nullable CommitFileListModel getByCommitId(@NonNull String id) {
        return files.get(id);
    }

    void clear() {
        files.clear();
    }
}
