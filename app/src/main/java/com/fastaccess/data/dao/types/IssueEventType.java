package com.fastaccess.data.dao.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.google.gson.annotations.SerializedName;

public enum IssueEventType {
    assigned(R.drawable.ic_profile),
    closed(R.drawable.ic_issue_closed),
    commented(R.drawable.ic_comment),
    committed(R.drawable.ic_push),
    demilestoned(R.drawable.ic_milestone),
    head_ref_deleted(R.drawable.ic_trash),
    head_ref_restored(R.drawable.ic_redo),
    labeled(R.drawable.ic_label),
    locked(R.drawable.ic_lock),
    mentioned(R.drawable.ic_at),
    merged(R.drawable.ic_fork),
    milestoned(R.drawable.ic_milestone),
    referenced(R.drawable.ic_format_quote),
    renamed(R.drawable.ic_edit),
    reopened(R.drawable.ic_issue_opened),
    subscribed(R.drawable.ic_subscribe),
    unassigned(R.drawable.ic_profile),
    unlabeled(R.drawable.ic_label),
    unlocked(R.drawable.ic_unlock),
    unsubscribed(R.drawable.ic_eye_off),
    review_requested(R.drawable.ic_eye),
    review_dismissed(R.drawable.ic_eye_off),
    review_request_removed(R.drawable.ic_eye_off),
    @SerializedName("cross-referenced")cross_referenced(R.drawable.ic_format_quote),
    @SerializedName("line-commented")line_commented(R.drawable.ic_comment),
    @SerializedName("commit-commented")commit_commented(R.drawable.ic_comment),
    reviewed(R.drawable.ic_eye),
    changes_requested(R.drawable.ic_eye),
    added_to_project(R.drawable.ic_add),
    GROUPED(R.drawable.ic_eye),
    deployed(R.drawable.ic_rocket);

    int iconResId;

    IssueEventType(int iconResId) {this.iconResId = iconResId;}

    public int getIconResId() {
        return iconResId == 0 ? R.drawable.ic_label : iconResId;
    }

    @Nullable public static IssueEventType getType(@NonNull String type) {
        return Stream.of(values())
                .filter(value -> value.name().toLowerCase().equalsIgnoreCase(type.toLowerCase()
                        .replaceAll("-", "_")))
                .findFirst()
                .orElse(null);
    }
}