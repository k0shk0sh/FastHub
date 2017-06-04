package com.fastaccess.helper;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.fastaccess.helper.BundleConstant.ExtraTYpe.EDIT_GIST_COMMENT_EXTRA;
import static com.fastaccess.helper.BundleConstant.ExtraTYpe.FOR_RESULT_EXTRA;
import static com.fastaccess.helper.BundleConstant.ExtraTYpe.NEW_GIST_COMMENT_EXTRA;

/**
 * Created by Kosh on 12 Nov 2016, 3:55 PM
 */

public class BundleConstant {
    public static final String ITEM = "item";
    public static final String ID = "id";
    public static final String EXTRA = "extra";
    public static final String EXTRA_TWO = "extra2_id";
    public static final String EXTRA_THREE = "extra3_id";
    public static final String EXTRA_FOUR = "extra4_id";
    public static final String EXTRA_FIVE = "extra5_id";
    public static final String EXTRA_SIX = "extra6_id";
    public static final String EXTRA_SEVEN = "extra7_id";
    public static final String EXTRA_EIGHT = "extra8_id";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String YES_NO_EXTRA = "yes_no_extra";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String REVIEW_EXTRA = "review_extra";
    public static final int REQUEST_CODE = 2016;
    public static final int REVIEW_REQUEST_CODE = 2017;


    @StringDef({
            NEW_GIST_COMMENT_EXTRA,
            EDIT_GIST_COMMENT_EXTRA,
            FOR_RESULT_EXTRA
    })

    @Retention(RetentionPolicy.SOURCE) public @interface ExtraTYpe {
        String FOR_RESULT_EXTRA = "for_result_extra";
        String EDIT_GIST_COMMENT_EXTRA = "edit_comment_extra";
        String NEW_GIST_COMMENT_EXTRA = "new_gist_comment_extra";
        String EDIT_ISSUE_COMMENT_EXTRA = "edit_issue_comment_extra";
        String NEW_ISSUE_COMMENT_EXTRA = "new_issue_comment_extra";
        String EDIT_COMMIT_COMMENT_EXTRA = "edit_commit_comment_extra";
        String NEW_COMMIT_COMMENT_EXTRA = "new_commit_comment_extra";
        String NEW_REVIEW_COMMENT_EXTRA = "new_review_comment_extra";
        String EDIT_REVIEW_COMMENT_EXTRA = "edit_review_comment_extra";
    }
}
