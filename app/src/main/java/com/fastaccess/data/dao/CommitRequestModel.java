package com.fastaccess.data.dao;

/**
 * Created by kosh on 31/08/2017.
 */

public class CommitRequestModel {

    private String message;
    private String content;
    private String sha;
    private String branch;

    public CommitRequestModel(String message, String content, String sha, String branch) {
        this.message = message;
        this.content = content;
        this.sha = sha;
        this.branch = branch;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
