package com.fastaccess.data.dao.transformer;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.GithubFileModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.siimkinks.sqlitemagic.annotation.transformer.DbValueToObject;
import com.siimkinks.sqlitemagic.annotation.transformer.ObjectToDbValue;
import com.siimkinks.sqlitemagic.annotation.transformer.Transformer;

import java.lang.reflect.Type;

@Transformer public final class MapTransformer {

    @ObjectToDbValue @NonNull public static String objectToDbValue(GithubFileModel javaObject) {
        return javaObject == null ? "{}" : new Gson().toJson(javaObject);
    }

    @DbValueToObject @NonNull public static GithubFileModel dbValueToObject(String dbObject) {
        Type type = new TypeToken<GithubFileModel>() {}.getType();
        return dbObject != null ? new Gson().fromJson(dbObject, type) : new GithubFileModel();
    }
}