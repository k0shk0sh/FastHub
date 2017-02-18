package com.fastaccess.data.dao.transformer;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.ReleasesAssetsListModel;
import com.google.gson.Gson;
import com.siimkinks.sqlitemagic.annotation.transformer.DbValueToObject;
import com.siimkinks.sqlitemagic.annotation.transformer.ObjectToDbValue;
import com.siimkinks.sqlitemagic.annotation.transformer.Transformer;

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */

@Transformer public class ReleasesAssetsTransformer {

    @ObjectToDbValue @NonNull public static String objectToDbValue(ReleasesAssetsListModel javaObject) {
        return javaObject == null ? "[]" : new Gson().toJson(javaObject);
    }

    @DbValueToObject @NonNull public static ReleasesAssetsListModel dbValueToObject(String dbObject) {
        return dbObject != null ? new Gson().fromJson(dbObject, ReleasesAssetsListModel.class) : new ReleasesAssetsListModel();
    }
}
