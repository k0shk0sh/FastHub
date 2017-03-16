package com.fastaccess.data.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.RepoFile;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.NoArgsConstructor;

/**
 * Created by Kosh on 03 Mar 2017, 10:43 PM
 */

@NoArgsConstructor
public class RepoPathsManager {
    private HashMap<String, ArrayList<RepoFile>> files = new HashMap<>();

    @Nullable public ArrayList<RepoFile> getPaths(@NonNull String url, @NonNull String ref) {
        return files.get(ref + "/" + url);
    }

    public void setFiles(@NonNull String ref, @NonNull String path, @NonNull ArrayList<RepoFile> paths) {
        files.put(ref + "/" + path, paths);
    }

    public void clear() {
        files.clear();
    }
}
