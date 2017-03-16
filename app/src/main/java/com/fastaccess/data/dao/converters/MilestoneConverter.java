package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.MilestoneModel;

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */

public class MilestoneConverter extends BaseConverter<MilestoneModel> {

    @Override protected Class<? extends MilestoneModel> getTypeClass() {
        return MilestoneModel.class;
    }
}
