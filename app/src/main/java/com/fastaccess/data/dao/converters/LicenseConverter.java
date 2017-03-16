package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.LicenseModel;

/**
 * Created by Kosh on 15 Mar 2017, 8:33 PM
 */

public class LicenseConverter extends BaseConverter<LicenseModel> {
    @Override protected Class<? extends LicenseModel> getTypeClass() {
        return LicenseModel.class;
    }
}
