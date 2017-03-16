package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.GitCommitModel;

/**
 * Created by Kosh on 15 Mar 2017, 8:42 PM
 */

public class GitCommitConverter extends BaseConverter<GitCommitModel> {
    @Override protected Class<? extends GitCommitModel> getTypeClass() {
        return GitCommitModel.class;
    }
}
