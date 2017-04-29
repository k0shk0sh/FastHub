package com.fastaccess.data.dao;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 14 Mar 2017, 9:10 PM
 */

@Getter @Setter @NoArgsConstructor
public class RepoSubscriptionModel {
    private boolean subscribed;
    private boolean ignored;
    private Date createdAt;
    private String url;
    private String repositoryUrl;
}
