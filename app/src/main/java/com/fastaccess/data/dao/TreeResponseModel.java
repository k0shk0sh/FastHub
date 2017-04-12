package com.fastaccess.data.dao;

import com.fastaccess.data.dao.model.RepoFile;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 12 Apr 2017, 1:12 PM
 */

@NoArgsConstructor @Getter @Setter public class TreeResponseModel {
    private String sha;
    private String url;
    boolean truncated;
    List<RepoFile> tree;
}
