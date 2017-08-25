// Copyright 2017 Google, Inc.
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

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Basic end to end test for the JobDispatcher. Requires Google Play services be installed and
 * available.
 */
@RunWith(AndroidJUnit4.class)
public final class EndToEndTest {
    private Context appContext;
    private FirebaseJobDispatcher dispatcher;

    @Before public void setUp() {
        appContext = InstrumentationRegistry.getTargetContext();
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(appContext));
        TestJobService.reset();
    }

    @Test public void basicImmediateJob() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        TestJobService.setProxy(new TestJobService.JobServiceProxy() {
            @Override
            public boolean onStartJob(JobParameters params) {
                latch.countDown();
                return false;
            }

            @Override
            public boolean onStopJob(JobParameters params) {
                return false;
            }
        });

        dispatcher.mustSchedule(
                dispatcher.newJobBuilder()
                        .setService(TestJobService.class)
                        .setTrigger(Trigger.NOW)
                        .setTag("basic-immediate-job")
                        .build());

        assertTrue("Latch wasn't counted down as expected", latch.await(120, TimeUnit.SECONDS));
    }
}
