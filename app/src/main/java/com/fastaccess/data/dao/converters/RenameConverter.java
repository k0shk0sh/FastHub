package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.RenameModel;

/**
 * Created by Kosh on 15 Mar 2017, 8:29 PM
 */

public class RenameConverter extends BaseConverter<RenameModel> {
    @Override protected Class<? extends RenameModel> getTypeClass() {
        return RenameModel.class;
    }
}
