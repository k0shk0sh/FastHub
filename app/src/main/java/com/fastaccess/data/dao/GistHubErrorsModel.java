package com.fastaccess.data.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 18 Feb 2017, 2:10 PM
 */

@Getter @Setter @NoArgsConstructor class GistHubErrorsModel {
    private String resource;
    private String field;
    private String code;
}
