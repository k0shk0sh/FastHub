package com.fastaccess.data.dao;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 05 Mar 2017, 12:01 PM
 */

@Getter @Setter @NoArgsConstructor
public class AssigneesRequestModel {
    private List<String> assignees;
    private List<String> reviewers;
}
