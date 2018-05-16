package com.fastaccess.data.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 10.02.18.
 */
@NoArgsConstructor @AllArgsConstructor @Getter @Setter public class LockIssuePrModel {
    private boolean locked;
    private String activeLockReason;
}
