package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.PayloadModel;

/**
 * Created by Kosh on 15 Mar 2017, 8:39 PM
 */

public class PayloadConverter extends BaseConverter<PayloadModel> {
    @Override protected Class<? extends PayloadModel> getTypeClass() {
        return PayloadModel.class;
    }
}
