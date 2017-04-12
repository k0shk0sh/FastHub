package com.fastaccess.data.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.RepoFile;

import java.util.HashMap;
import java.util.List;

import lombok.NoArgsConstructor;

/**
 * Created by Kosh on 03 Mar 2017, 10:43 PM
 */

@NoArgsConstructor
public class RepoPathsManager {
    private HashMap<String, List<RepoFile>> files = new HashMap<>();

    @Nullable public List<RepoFile> getPaths(@NonNull String url, @NonNull String ref) {
        return files.get(ref + "/" + url);
    }

    public void setFiles(@NonNull String ref, @NonNull String path, @NonNull List<RepoFile> paths) {
        files.put(ref + "/" + path, paths);
    }

    public void clear() {
        files.clear();
    }
}
