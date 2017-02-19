package com.fastaccess.data.dao.transformer;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.types.NotificationReason;
import com.siimkinks.sqlitemagic.annotation.transformer.DbValueToObject;
import com.siimkinks.sqlitemagic.annotation.transformer.ObjectToDbValue;
import com.siimkinks.sqlitemagic.annotation.transformer.Transformer;

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */

@Transformer public class NotificatinReasonsTransformer {

    @ObjectToDbValue @NonNull public static String objectToDbValue(NotificationReason javaObject) {
        return javaObject == null ? NotificationReason.mention.name() : javaObject.name();
    }

    @DbValueToObject @NonNull public static NotificationReason dbValueToObject(String dbObject) {
        return dbObject != null ? NotificationReason.valueOf(dbObject) : NotificationReason.mention;
    }
}
