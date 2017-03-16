package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.LabelModel;

/**
 * Created by Kosh on 15 Mar 2017, 8:30 PM
 */

public class LabelConverter extends BaseConverter<LabelModel> {
    @Override protected Class<? extends LabelModel> getTypeClass() {
        return LabelModel.class;
    }
}
