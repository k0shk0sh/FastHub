package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.NotificationSubjectModel;

/**
 * Created by Kosh on 15 Mar 2017, 7:58 PM
 */

public class NotificationSubjectConverter extends BaseConverter<NotificationSubjectModel> {

    @Override protected Class<? extends NotificationSubjectModel> getTypeClass() {
        return NotificationSubjectModel.class;
    }
}
