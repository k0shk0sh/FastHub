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
import static com.firebase.jobdispatcher.GooglePlayReceiver.TAG;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import com.firebase.jobdispatcher.JobInvocation.Builder;

/**
 * A messenger for communication with GCM Network Scheduler.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class GooglePlayMessageHandler extends Handler {

    static final int MSG_START_EXEC = 1;
    static final int MSG_STOP_EXEC = 2;
    static final int MSG_RESULT = 3;
    private static final int MSG_INIT = 4;
    private final GooglePlayReceiver googlePlayReceiver;

    public GooglePlayMessageHandler(Looper mainLooper, GooglePlayReceiver googlePlayReceiver) {
        super(mainLooper);
        this.googlePlayReceiver = googlePlayReceiver;
    }

    @Override
    public void handleMessage(Message message) {
        if (message == null) {
            return;
        }

        AppOpsManager appOpsManager = (AppOpsManager) googlePlayReceiver.getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);
        try {
            appOpsManager.checkPackage(message.sendingUid, GooglePlayDriver.BACKEND_PACKAGE);
        } catch (SecurityException e) {
            Log.e(TAG, "Message was not sent from GCM.");
            return;
        }

        switch (message.what) {
            case MSG_START_EXEC:
                handleStartMessage(message);
                break;

            case MSG_STOP_EXEC:
                handleStopMessage(message);
                break;

            case MSG_INIT:
                // Not implemented.
                break;

            default:
                Log.e(TAG, "Unrecognized message received: " + message);
                break;
        }
    }

    private void handleStartMessage(Message message) {
        final Bundle data = message.getData();

        final Messenger replyTo = message.replyTo;
        String tag = data.getString(REQUEST_PARAM_TAG);
        if (replyTo == null || tag == null) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Invalid start execution message.");
            }
            return;
        }

        GooglePlayMessengerCallback messengerCallback =
                new GooglePlayMessengerCallback(replyTo, tag);
        JobInvocation jobInvocation = googlePlayReceiver.prepareJob(messengerCallback, data);
        googlePlayReceiver.getExecutionDelegator().executeJob(jobInvocation);
    }

    private void handleStopMessage(Message message) {
        Builder builder = GooglePlayReceiver.getJobCoder().decode(message.getData());
        if (builder == null) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Invalid stop execution message.");
            }
            return;
        }
        JobInvocation job = builder.build();
        googlePlayReceiver.getExecutionDelegator().stopJob(job);
    }
}
