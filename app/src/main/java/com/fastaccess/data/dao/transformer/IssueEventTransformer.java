package com.fastaccess.data.dao.transformer;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.types.IssueEventType;
import com.siimkinks.sqlitemagic.annotation.transformer.DbValueToObject;
import com.siimkinks.sqlitemagic.annotation.transformer.ObjectToDbValue;
import com.siimkinks.sqlitemagic.annotation.transformer.Transformer;

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */

@Transformer public class IssueEventTransformer {

    @ObjectToDbValue @NonNull public static String objectToDbValue(IssueEventType javaObject) {
        return javaObject == null ? IssueEventType.labeled.name() : javaObject.name();
    }

    @DbValueToObject @NonNull public static IssueEventType dbValueToObject(String dbObject) {
        return dbObject != null ? IssueEventType.valueOf(dbObject) : IssueEventType.labeled;
    }
}
