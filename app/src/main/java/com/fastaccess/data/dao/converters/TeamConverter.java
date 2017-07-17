package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.TeamsModel;
import com.fastaccess.data.dao.model.User;

/**
 * Created by Kosh on 15 Mar 2017, 7:58 PM
 */

public class TeamConverter extends BaseConverter<TeamsModel> {

    @Override protected Class<? extends TeamsModel> getTypeClass() {
        return TeamsModel.class;
    }
}
