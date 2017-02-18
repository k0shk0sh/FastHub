package com.fastaccess.data.dao.types;


public enum EventsType {
    WatchEvent("starred"),
    CreateEvent("created repository"),
    CommitCommentEvent("committed"),
    DownloadEvent("downloaded"),
    FollowEvent("followed"),
    ForkEvent("forked"),
    GistEvent("created gist"),
    GollumEvent("gollum"),
    IssueCommentEvent("commented on issue"),
    IssuesEvent("created issue"),
    MemberEvent("member"),
    PublicEvent("public"),
    PullRequestEvent("pull request"),
    PullRequestReviewCommentEvent("PR comment preview"),
    PushEvent("pushed"),
    StatusEvent("status"),
    TeamAddEvent("team"),
    DeleteEvent("deleted"),
    ReleaseEvent("released"),
    Unhandled("unknown");
    String type;

    EventsType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}