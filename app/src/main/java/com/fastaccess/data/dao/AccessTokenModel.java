package com.fastaccess.data.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 09 Nov 2016, 11:28 PM
 */


@Getter @Setter @NoArgsConstructor
public class AccessTokenModel {
    private String accessToken;
    private String tokenType;
}
