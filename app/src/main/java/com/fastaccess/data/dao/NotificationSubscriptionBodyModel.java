package com.fastaccess.data.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Hashemsergani on 14.10.17.
 */

@AllArgsConstructor @Getter @Setter public class NotificationSubscriptionBodyModel {
    private Boolean subscribed;
    private Boolean ignored;
}
