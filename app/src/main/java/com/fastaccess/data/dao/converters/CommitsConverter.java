package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.CommitListModel;

/**
 * Created by Kosh on 15 Mar 2017, 8:37 PM
 */

public class CommitsConverter extends BaseConverter<CommitListModel> {
    @Override protected Class<? extends CommitListModel> getTypeClass() {
        return CommitListModel.class;
    }
}
