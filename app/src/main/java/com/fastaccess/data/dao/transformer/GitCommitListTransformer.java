package com.fastaccess.data.dao.transformer;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.GitCommitListModel;
import com.google.gson.Gson;
import com.siimkinks.sqlitemagic.annotation.transformer.DbValueToObject;
import com.siimkinks.sqlitemagic.annotation.transformer.ObjectToDbValue;
import com.siimkinks.sqlitemagic.annotation.transformer.Transformer;

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */

@Transformer public class GitCommitListTransformer {

    @ObjectToDbValue @NonNull public static String objectToDbValue(GitCommitListModel javaObject) {
        return javaObject == null ? "[]" : new Gson().toJson(javaObject);
    }

    @DbValueToObject @NonNull public static GitCommitListModel dbValueToObject(String dbObject) {
        return dbObject != null ? new Gson().fromJson(dbObject, GitCommitListModel.class) : new GitCommitListModel();
    }
}
