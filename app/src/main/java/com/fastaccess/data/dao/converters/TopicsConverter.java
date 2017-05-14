package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.TopicsModel;

/**
 * Created by Kosh on 09 May 2017, 7:54 PM
 */

public class TopicsConverter extends BaseConverter<TopicsModel> {
    @Override protected Class<? extends TopicsModel> getTypeClass() {
        return TopicsModel.class;
    }
}
