package com.fastaccess.data.dao;

/**
 * Created by kosh on 31/08/2017.
 */

public class CommitRequestModel {

    private String message;
    private String content;
    private String sha;

    public CommitRequestModel(String message, String content, String sha) {
        this.message = message;
        this.content = content;
        this.sha = sha;
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
}
