package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.model.Commit;

/**
 * Created by Kosh on 15 Mar 2017, 7:58 PM
 */

public class CommitConverter extends BaseConverter<Commit> {

    @Override protected Class<? extends Commit> getTypeClass() {
        return Commit.class;
    }
}
