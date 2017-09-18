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

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import android.util.Pair;
import com.firebase.jobdispatcher.Job.Builder;
import com.firebase.jobdispatcher.JobService.JobResult;
import com.firebase.jobdispatcher.JobTrigger.ContentUriTrigger;

/**
 * Handles incoming execute requests from the GooglePlay driver and forwards them to your Service.
 */
public class GooglePlayReceiver extends Service implements ExecutionDelegator.JobFinishedCallback {
    /**
     * Logging tag.
     */
    /* package */ static final String TAG = "FJD.GooglePlayReceiver";
    /**
     * The action sent by Google Play services that triggers job execution.
     */
    @VisibleForTesting
    static final String ACTION_EXECUTE = "com.google.android.gms.gcm.ACTION_TASK_READY";

    /** Action sent by Google Play services when your app has been updated. */
    @VisibleForTesting
    static final String ACTION_INITIALIZE = "com.google.android.gms.gcm.SERVICE_ACTION_INITIALIZE";

    private static final String ERROR_NULL_INTENT = "Null Intent passed, terminating";
    private static final String ERROR_UNKNOWN_ACTION = "Unknown action received, terminating";
    private static final String ERROR_NO_DATA = "No data provided, terminating";

    private static final JobCoder prefixedCoder =
        new JobCoder(BundleProtocol.PACKED_PARAM_BUNDLE_PREFIX, true);

    private final GooglePlayCallbackExtractor callbackExtractor = new GooglePlayCallbackExtractor();

    /**
     * The single Messenger that's returned from valid onBind requests. Guarded by intrinsic lock.
     */
    @VisibleForTesting
    Messenger serviceMessenger;

    /**
     * Driver for rescheduling jobs. Guarded by intrinsic lock.
     */
    @VisibleForTesting
    Driver driver;

    /**
     * Guarded by intrinsic lock.
     */
    @VisibleForTesting
    ValidationEnforcer validationEnforcer;

    /**
     * The ExecutionDelegator used to communicate with client JobServices.
     * Guarded by intrinsic lock.
     */
    private ExecutionDelegator executionDelegator;

    /**
     * The most recent startId passed to onStartCommand.
     * Guarded by intrinsic lock.
     */
    private int latestStartId;

    /**
     * Endpoint (String) -> Tag (String) -> JobCallback
     */
    private SimpleArrayMap<String, SimpleArrayMap<String, JobCallback>> callbacks =
        new SimpleArrayMap<>(1);

    private static void sendResultSafely(JobCallback callback, int result) {
        try {
            callback.jobFinished(result);
        } catch (Throwable e) {
            Log.e(TAG, "Encountered error running callback", e.getCause());
        }
    }

    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        try {
            super.onStartCommand(intent, flags, startId);

            if (intent == null) {
                Log.w(TAG, ERROR_NULL_INTENT);
                return START_NOT_STICKY;
            }

            String action = intent.getAction();
            if (ACTION_EXECUTE.equals(action)) {
                getExecutionDelegator().executeJob(prepareJob(intent));
                return START_NOT_STICKY;
            } else if (ACTION_INITIALIZE.equals(action)) {
                return START_NOT_STICKY;
            }

            Log.e(TAG, ERROR_UNKNOWN_ACTION);
            return START_NOT_STICKY;
        } finally {
            synchronized (this) {
                latestStartId = startId;
                if (callbacks.isEmpty()) {
                    stopSelf(latestStartId);
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Only Lollipop+ supports UID checking messages, so we can't trust this system on older
        // platforms.
        if (intent == null
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
                || !ACTION_EXECUTE.equals(intent.getAction())) {
            return null;
        }
        return getServiceMessenger().getBinder();
    }

    private synchronized Messenger getServiceMessenger() {
        if (serviceMessenger == null) {
            serviceMessenger =
                new Messenger(new GooglePlayMessageHandler(Looper.getMainLooper(), this));
        }
        return serviceMessenger;
    }

    /* package */ synchronized ExecutionDelegator getExecutionDelegator() {
        if (executionDelegator == null) {
            executionDelegator = new ExecutionDelegator(this, this);
        }
        return executionDelegator;
    }

    @NonNull
    private synchronized Driver getGooglePlayDriver() {
        if (driver == null) {
            driver = new GooglePlayDriver(getApplicationContext());
        }
        return driver;
    }

    @NonNull
    private synchronized ValidationEnforcer getValidationEnforcer() {
        if (validationEnforcer == null) {
            validationEnforcer = new ValidationEnforcer(getGooglePlayDriver().getValidator());
        }
        return validationEnforcer;
    }

    @Nullable
    @VisibleForTesting
    JobInvocation prepareJob(Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras == null) {
            Log.e(TAG, ERROR_NO_DATA);
            return null;
        }

        // get the callback first. If we don't have this we can't talk back to the backend.
        Pair<JobCallback, Bundle> extraction = callbackExtractor.extractCallback(intentExtras);
        if (extraction == null) {
            Log.i(TAG, "no callback found");
            return null;
        }
        return prepareJob(extraction.first, extraction.second);
    }

    @Nullable
    synchronized JobInvocation prepareJob(JobCallback callback, Bundle bundle) {
        JobInvocation job = prefixedCoder.decodeIntentBundle(bundle);
        if (job == null) {
            Log.e(TAG, "unable to decode job");
            sendResultSafely(callback, JobService.RESULT_FAIL_NORETRY);
            return null;
        }
        SimpleArrayMap<String, JobCallback> map = callbacks.get(job.getService());
        if (map == null) {
            map = new SimpleArrayMap<>(1);
            callbacks.put(job.getService(), map);
        }

        map.put(job.getTag(), callback);

        return job;
    }

    @Override
    public synchronized void onJobFinished(@NonNull JobInvocation js, @JobResult int result) {
        try {
            SimpleArrayMap<String, JobCallback> map = callbacks.get(js.getService());
            if (map == null) {
                return;
            }
            JobCallback callback = map.remove(js.getTag());
            if (callback == null) {
                return;
            }
            if (map.isEmpty()) {
                callbacks.remove(js.getService());
            }

            if (needsToBeRescheduled(js, result)) {
                reschedule(js);
            } else {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "sending jobFinished for " + js.getTag() + " = " + result);
                }
                sendResultSafely(callback, result);
            }
        } finally {
            if (callbacks.isEmpty()) {
                // Safe to call stopSelf, even if we're being bound to
                stopSelf(latestStartId);
            }
        }
    }

    private void reschedule(JobInvocation jobInvocation) {
        Job job = new Builder(getValidationEnforcer(), jobInvocation)
            .setReplaceCurrent(true)
            .build();

        getGooglePlayDriver().schedule(job);
    }

    /**
     * Recurring content URI triggered jobs need to be rescheduled when execution is finished.
     *
     * <p>GooglePlay does not support recurring content URI triggered jobs.
     *
     * <p>{@link JobService#RESULT_FAIL_RETRY} needs to be sent or current triggered URIs will be
     * lost.
     */
    private static boolean needsToBeRescheduled(JobParameters job, int result) {
        return job.isRecurring()
            && job.getTrigger() instanceof ContentUriTrigger
            && result != JobService.RESULT_FAIL_RETRY;
    }

    static JobCoder getJobCoder() {
        return prefixedCoder;
    }
}
