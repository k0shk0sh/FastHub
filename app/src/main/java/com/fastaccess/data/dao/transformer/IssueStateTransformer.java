package com.fastaccess.data.dao.transformer;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.types.IssueState;
import com.siimkinks.sqlitemagic.annotation.transformer.DbValueToObject;
import com.siimkinks.sqlitemagic.annotation.transformer.ObjectToDbValue;
import com.siimkinks.sqlitemagic.annotation.transformer.Transformer;

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */

@Transformer public class IssueStateTransformer {

    @ObjectToDbValue @NonNull public static String objectToDbValue(IssueState javaObject) {
        return javaObject == null ? IssueState.closed.name() : javaObject.name();
    }

    @DbValueToObject @NonNull public static IssueState dbValueToObject(String dbObject) {
        return dbObject != null ? IssueState.valueOf(dbObject) : IssueState.closed;
    }
}
