package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.ReleasesAssetsListModel;

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */

public class ReleasesAssetsConverter extends BaseConverter<ReleasesAssetsListModel> {

    @Override protected Class<? extends ReleasesAssetsListModel> getTypeClass() {
        return ReleasesAssetsListModel.class;
    }
}
