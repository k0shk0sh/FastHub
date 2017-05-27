package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.ReactionsModel;

/**
 * Created by Kosh on 06 May 2017, 4:53 PM
 */

public class ReactionsConverter extends BaseConverter<ReactionsModel> {
    @Override protected Class<? extends ReactionsModel> getTypeClass() {
        return ReactionsModel.class;
    }
}
