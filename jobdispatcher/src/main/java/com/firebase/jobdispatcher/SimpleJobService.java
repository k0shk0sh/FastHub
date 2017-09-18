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

import android.os.AsyncTask;
import android.support.annotation.CallSuper;
import android.support.v4.util.SimpleArrayMap;

/**
 * SimpleJobService provides a simple way of doing background work in a JobService.
 *
 * Users should override onRunJob and return one of the {@link JobResult} ints.
 */
public abstract class SimpleJobService extends JobService {
    private final SimpleArrayMap<JobParameters, AsyncJobTask> runningJobs =
        new SimpleArrayMap<>();

    @CallSuper
    @Override
    public boolean onStartJob(JobParameters job) {
        AsyncJobTask async = new AsyncJobTask(this, job);

        synchronized (runningJobs) {
            runningJobs.put(job, async);
        }

        async.execute();

        return true; // more work to do
    }

    @CallSuper
    @Override
    public boolean onStopJob(JobParameters job) {
        synchronized (runningJobs) {
            AsyncJobTask async = runningJobs.remove(job);
            if (async != null) {
                async.cancel(true);
                return true;
            }
        }

        return false;
    }

    private void onJobFinished(JobParameters jobParameters, boolean b) {
        synchronized (runningJobs) {
            runningJobs.remove(jobParameters);
        }

        jobFinished(jobParameters, b);
    }

    @JobResult
    public abstract int onRunJob(JobParameters job);

    private static class AsyncJobTask extends AsyncTask<Void, Void, Integer> {
        private final SimpleJobService jobService;
        private final JobParameters jobParameters;

        private AsyncJobTask(SimpleJobService jobService, JobParameters jobParameters) {
            this.jobService = jobService;
            this.jobParameters = jobParameters;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return jobService.onRunJob(jobParameters);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            jobService.onJobFinished(jobParameters, integer == JobService.RESULT_FAIL_RETRY);
        }
    }
}
