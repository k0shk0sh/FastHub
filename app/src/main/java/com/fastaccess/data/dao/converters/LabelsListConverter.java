package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.LabelListModel;

/**
 * Created by Kosh on 11 Feb 2017, 11:43 PM
 */

public class LabelsListConverter extends BaseConverter<LabelListModel> {

    @Override protected Class<? extends LabelListModel> getTypeClass() {
        return LabelListModel.class;
    }
}
