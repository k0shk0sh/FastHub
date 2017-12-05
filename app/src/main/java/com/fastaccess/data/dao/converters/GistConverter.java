package com.fastaccess.data.dao.converters;

import com.fastaccess.data.dao.model.Gist;

/**
 * Created by Kosh on 15 Mar 2017, 8:30 PM
 */

public class GistConverter extends BaseConverter<Gist> {
    @Override protected Class<? extends Gist> getTypeClass() {
        return Gist.class;
    }
}
