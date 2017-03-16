package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.UsersListModel;

/**
 * Created by Kosh on 15 Mar 2017, 8:26 PM
 */

public class UsersConverter extends BaseConverter<UsersListModel> {
    @Override protected Class<? extends UsersListModel> getTypeClass() {
        return UsersListModel.class;
    }
}
