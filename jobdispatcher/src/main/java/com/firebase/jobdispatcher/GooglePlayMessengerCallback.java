// Copyright 2016 Google, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package com.firebase.jobdispatcher;

import static com.firebase.jobdispatcher.GooglePlayJobWriter.REQUEST_PARAM_TAG;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.firebase.jobdispatcher.JobService.JobResult;

/**
 * Wraps the GooglePlay messenger in a JobCallback-compatible interface.
 */
class GooglePlayMessengerCallback implements JobCallback {

    private final Messenger messenger;
    private final String tag;

    GooglePlayMessengerCallback(Messenger messenger, String tag) {
        this.messenger = messenger;
        this.tag = tag;
    }

    @Override
    public void jobFinished(@JobResult int status) {
        try {
            messenger.send(createResultMessage(status));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private Message createResultMessage(int result) {
        final Message msg = Message.obtain();
        msg.what = GooglePlayMessageHandler.MSG_RESULT;
        msg.arg1 = result;

        Bundle b = new Bundle();
        b.putString(REQUEST_PARAM_TAG, tag);
        msg.setData(b);
        return msg;
    }
}
