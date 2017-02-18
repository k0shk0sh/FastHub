package com.fastaccess.data.dao.transformer;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.types.FilesType;
import com.siimkinks.sqlitemagic.annotation.transformer.DbValueToObject;
import com.siimkinks.sqlitemagic.annotation.transformer.ObjectToDbValue;
import com.siimkinks.sqlitemagic.annotation.transformer.Transformer;

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */

@Transformer public class FilesTypeTransformer {

    @ObjectToDbValue @NonNull public static String objectToDbValue(FilesType javaObject) {
        return javaObject == null ? FilesType.file.name() : javaObject.name();
    }

    @DbValueToObject @NonNull public static FilesType dbValueToObject(String dbObject) {
        return dbObject != null ? FilesType.valueOf(dbObject) : FilesType.file;
    }
}
