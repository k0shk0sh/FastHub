package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.GithubFileModel;

/**
 * Created by Kosh on 15 Mar 2017, 8:21 PM
 */

public class GitHubFilesConverter extends BaseConverter<GithubFileModel> {
    @Override protected Class<? extends GithubFileModel> getTypeClass() {
        return GithubFileModel.class;
    }
}
