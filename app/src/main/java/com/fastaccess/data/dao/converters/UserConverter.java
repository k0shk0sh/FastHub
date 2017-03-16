package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.model.User;

/**
 * Created by Kosh on 15 Mar 2017, 7:58 PM
 */

public class UserConverter extends BaseConverter<User> {

    @Override protected Class<? extends User> getTypeClass() {
        return User.class;
    }
}
