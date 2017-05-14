package com.fastaccess.ui.adapter.callback;

/**
 * Created by Kosh on 03 Apr 2017, 2:52 PM
 */

public interface ReactionsCallback {
    boolean isPreviouslyReacted(long id, int vId);

    boolean isCallingApi(long id, int vId);
}
