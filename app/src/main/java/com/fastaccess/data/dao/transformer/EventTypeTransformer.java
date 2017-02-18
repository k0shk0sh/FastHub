package com.fastaccess.data.dao.transformer;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.types.EventsType;
import com.siimkinks.sqlitemagic.annotation.transformer.DbValueToObject;
import com.siimkinks.sqlitemagic.annotation.transformer.ObjectToDbValue;
import com.siimkinks.sqlitemagic.annotation.transformer.Transformer;

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */

@Transformer public class EventTypeTransformer {

    @ObjectToDbValue @NonNull public static String objectToDbValue(EventsType javaObject) {
        return javaObject == null ? EventsType.WatchEvent.name() : javaObject.name();
    }

    @DbValueToObject @NonNull public static EventsType dbValueToObject(String dbObject) {
        return dbObject != null ? EventsType.valueOf(dbObject) : EventsType.WatchEvent;
    }
}
