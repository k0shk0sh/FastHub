package com.fastaccess.data.dao.converters;

import com.fastaccess.provider.rest.RestProvider;

import io.requery.Converter;

/**
 * Created by Kosh on 15 Mar 2017, 8:02 PM
 */

public abstract class BaseConverter<C> implements Converter<C, String> {

    protected abstract Class<? extends C> getTypeClass();

    @SuppressWarnings("unchecked") @Override public Class<C> getMappedType() {
        return (Class<C>) getTypeClass();
    }

    @Override public Class<String> getPersistedType() {
        return String.class;
    }

    @Override public Integer getPersistedSize() {
        return null;
    }

    @Override public String convertToPersisted(C value) {
        return RestProvider.gson.toJson(value);
    }

    @Override public C convertToMapped(Class<? extends C> type, String value) {
        return RestProvider.gson.fromJson(value, type);
    }
}
