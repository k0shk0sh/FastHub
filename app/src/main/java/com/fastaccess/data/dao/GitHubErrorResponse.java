package com.fastaccess.data.dao;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 18 Feb 2017, 2:09 PM
 */

@Getter @Setter @NoArgsConstructor
public class GitHubErrorResponse {
    private String message;
    private String documentation_url;
    private List<GistHubErrorsModel> errors;

    @Override public String toString() {
        return "GitHubErrorResponse{" +
                "message='" + message + '\'' +
                ", documentation_url='" + documentation_url + '\'' +
                ", errors=" + errors +
                '}';
    }
}
