package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.model.Repo;

/**
 * Created by Kosh on 15 Mar 2017, 7:58 PM
 */

public class RepoConverter extends BaseConverter<Repo> {

    @Override protected Class<? extends Repo> getTypeClass() {
        return Repo.class;
    }
}
