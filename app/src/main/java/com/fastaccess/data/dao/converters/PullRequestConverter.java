package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.model.PullRequest;

/**
 * Created by Kosh on 15 Mar 2017, 7:58 PM
 */

public class PullRequestConverter extends BaseConverter<PullRequest> {

    @Override protected Class<? extends PullRequest> getTypeClass() {
        return PullRequest.class;
    }
}
