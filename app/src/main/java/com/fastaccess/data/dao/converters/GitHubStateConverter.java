package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.GithubState;

/**
 * Created by Kosh on 15 Mar 2017, 8:41 PM
 */

public class GitHubStateConverter extends BaseConverter<GithubState> {
    @Override protected Class<? extends GithubState> getTypeClass() {
        return GithubState.class;
    }
}
