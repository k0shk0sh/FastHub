package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.RepoPermissionsModel;

/**
 * Created by Kosh on 15 Mar 2017, 8:33 PM
 */

public class RepoPermissionConverter extends BaseConverter<RepoPermissionsModel> {
    @Override protected Class<? extends RepoPermissionsModel> getTypeClass() {
        return RepoPermissionsModel.class;
    }
}
