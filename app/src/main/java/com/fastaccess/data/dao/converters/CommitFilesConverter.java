package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.CommitFileListModel;

/**
 * Created by Kosh on 15 Mar 2017, 8:37 PM
 */

public class CommitFilesConverter extends BaseConverter<CommitFileListModel> {
    @Override protected Class<? extends CommitFileListModel> getTypeClass() {
        return CommitFileListModel.class;
    }
}
