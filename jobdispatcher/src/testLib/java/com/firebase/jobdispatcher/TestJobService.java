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

/** A very simple JobService that can be configured for individual tests. */
public class TestJobService extends JobService {

    public interface JobServiceProxy {
        boolean onStartJob(JobParameters job);

        boolean onStopJob(JobParameters job);
    }

    public static final JobServiceProxy NOOP_PROXY =
        new JobServiceProxy() {
            @Override
            public boolean onStartJob(JobParameters job) {
                return false;
            }

            @Override
            public boolean onStopJob(JobParameters job) {
                return false;
            }
        };

    private static final Object lock = new Object();

    // GuardedBy("lock")
    private static JobServiceProxy currentProxy = NOOP_PROXY;

    public static void setProxy(JobServiceProxy proxy) {
        synchronized (lock) {
            currentProxy = proxy;
        }
    }

    public static void reset() {
        synchronized (lock) {
            currentProxy = NOOP_PROXY;
        }
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        synchronized (lock) {
            return currentProxy.onStartJob(job);
        }
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        synchronized (lock) {
            return currentProxy.onStopJob(job);
        }
    }
}
