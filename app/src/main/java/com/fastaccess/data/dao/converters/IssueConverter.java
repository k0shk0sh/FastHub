package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.model.Issue;

/**
 * Created by Kosh on 15 Mar 2017, 8:30 PM
 */

public class IssueConverter extends BaseConverter<Issue> {
    @Override protected Class<? extends Issue> getTypeClass() {
        return Issue.class;
    }
}
