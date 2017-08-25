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

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import com.firebase.jobdispatcher.JobService.JobResult;
import java.lang.ref.WeakReference;

/**
 * ExecutionDelegator tracks local Binder connections to client JobServices and handles
 * communication with those services.
 */
/* package */ class ExecutionDelegator {
    @VisibleForTesting
    static final int JOB_FINISHED = 1;

    static final String TAG = "FJD.ExternalReceiver";

    interface JobFinishedCallback {
        void onJobFinished(@NonNull JobInvocation jobInvocation, @JobResult int result);
    }

    /**
     * A mapping of {@link JobInvocation} to (local) binder connections.
     * Synchronized by itself.
     */
    private final SimpleArrayMap<JobInvocation, JobServiceConnection> serviceConnections =
        new SimpleArrayMap<>();
    private final ResponseHandler responseHandler =
        new ResponseHandler(Looper.getMainLooper(), new WeakReference<>(this));
    private final Context context;
    private final JobFinishedCallback jobFinishedCallback;

    ExecutionDelegator(Context context, JobFinishedCallback jobFinishedCallback) {
        this.context = context;
        this.jobFinishedCallback = jobFinishedCallback;
    }

    /**
     * Executes the provided {@code jobInvocation} by kicking off the creation of a new Binder
     * connection to the Service.
     *
     * @return true if the service was bound successfully.
     */
    boolean executeJob(JobInvocation jobInvocation) {
        if (jobInvocation == null) {
            return false;
        }

        JobServiceConnection conn = new JobServiceConnection(jobInvocation,
            responseHandler.obtainMessage(JOB_FINISHED));

        synchronized (serviceConnections) {
            JobServiceConnection oldConnection = serviceConnections.put(jobInvocation, conn);
            if (oldConnection != null) {
                Log.e(TAG, "Received execution request for already running job");
            }
            return context.bindService(createBindIntent(jobInvocation), conn, BIND_AUTO_CREATE);
        }
    }

    @NonNull
    private Intent createBindIntent(JobParameters jobParameters) {
        Intent execReq = new Intent(JobService.ACTION_EXECUTE);
        execReq.setClassName(context, jobParameters.getService());
        return execReq;
    }

    void stopJob(JobInvocation job) {
        synchronized (serviceConnections) {
            JobServiceConnection jobServiceConnection = serviceConnections.remove(job);
            if (jobServiceConnection != null) {
                jobServiceConnection.onStop();
                safeUnbindService(jobServiceConnection);
            }
        }
    }

    private void safeUnbindService(JobServiceConnection connection) {
        if (connection != null && connection.isBound()) {
            try {
                context.unbindService(connection);
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Error unbinding service: " + e.getMessage());
            }
        }
    }

    private void onJobFinishedMessage(JobInvocation jobInvocation, int result) {
        synchronized (serviceConnections) {
            JobServiceConnection connection = serviceConnections.remove(jobInvocation);
            safeUnbindService(connection);
        }

        jobFinishedCallback.onJobFinished(jobInvocation, result);
    }

    private static class ResponseHandler extends Handler {

        /**
         * We hold a WeakReference to the ExecutionDelegator because it holds a reference to a
         * Service Context and Handlers are often kept in memory longer than you'd expect because
         * any pending Messages can maintain references to them.
         */
        private final WeakReference<ExecutionDelegator> executionDelegatorReference;

        ResponseHandler(Looper looper, WeakReference<ExecutionDelegator> executionDelegator) {
            super(looper);
            this.executionDelegatorReference = executionDelegator;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case JOB_FINISHED:
                    if (msg.obj instanceof JobInvocation) {
                        ExecutionDelegator delegator = this.executionDelegatorReference.get();
                        if (delegator == null) {
                            Log.wtf(TAG, "handleMessage: service was unexpectedly GC'd"
                                + ", can't send job result");
                            return;
                        }

                        delegator.onJobFinishedMessage((JobInvocation) msg.obj, msg.arg1);
                        return;
                    }

                    Log.wtf(TAG, "handleMessage: unknown obj returned");
                    return;

                default:
                    Log.wtf(TAG, "handleMessage: unknown message type received: " + msg.what);
            }
        }
    }
}
