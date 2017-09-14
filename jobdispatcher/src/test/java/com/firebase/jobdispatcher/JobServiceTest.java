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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import com.firebase.jobdispatcher.JobInvocation.Builder;
import com.google.android.gms.gcm.PendingCallback;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE, sdk = 23)
public class JobServiceTest {
    private static CountDownLatch countDownLatch;

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {
        countDownLatch = null;
    }

    @Test
    public void testOnStartCommand_handlesNullIntent() throws Exception {
        JobService service = spy(new ExampleJobService());
        int startId = 7;

        try {
            service.onStartCommand(null, 0, startId);

            verify(service).stopSelf(startId);
        } catch (NullPointerException npe) {
            fail("Unexpected NullPointerException after calling onStartCommand with a null Intent.");
        }
    }

    @Test
    public void testOnStartCommand_handlesNullAction() throws Exception {
        JobService service = spy(new ExampleJobService());
        int startId = 7;

        Intent nullActionIntent = new Intent();
        service.onStartCommand(nullActionIntent, 0, startId);

        verify(service).stopSelf(startId);
    }

    @Test
    public void testOnStartCommand_handlesEmptyAction() throws Exception {
        JobService service = spy(new ExampleJobService());
        int startId = 7;

        Intent emptyActionIntent = new Intent("");
        service.onStartCommand(emptyActionIntent, 0, startId);

        verify(service).stopSelf(startId);
    }

    @Test
    public void testOnStartCommand_handlesUnknownAction() throws Exception {
        JobService service = spy(new ExampleJobService());
        int startId = 7;

        Intent emptyActionIntent = new Intent("foo.bar.baz");
        service.onStartCommand(emptyActionIntent, 0, startId);

        verify(service).stopSelf(startId);
    }

    @Test
    public void testOnStartCommand_handlesStartJob_nullData() {
        JobService service = spy(new ExampleJobService());
        int startId = 7;

        Intent executeJobIntent = new Intent(JobService.ACTION_EXECUTE);
        service.onStartCommand(executeJobIntent, 0, startId);

        verify(service).stopSelf(startId);
    }

    @Test
    public void testOnStartCommand_handlesStartJob_noTag() {
        JobService service = spy(new ExampleJobService());
        int startId = 7;

        Intent executeJobIntent = new Intent(JobService.ACTION_EXECUTE);
        Parcel p = Parcel.obtain();
        p.writeStrongBinder(mock(IBinder.class));
        executeJobIntent.putExtra("callback", new PendingCallback(p));

        service.onStartCommand(executeJobIntent, 0, startId);

        verify(service).stopSelf(startId);

        p.recycle();
    }

    @Test
    public void testOnStartCommand_handlesStartJob_noCallback() {
        JobService service = spy(new ExampleJobService());
        int startId = 7;

        Intent executeJobIntent = new Intent(JobService.ACTION_EXECUTE);
        executeJobIntent.putExtra("tag", "foobar");

        service.onStartCommand(executeJobIntent, 0, startId);

        verify(service).stopSelf(startId);
    }

    @Test
    public void testOnStartCommand_handlesStartJob_validRequest() throws InterruptedException {
        JobService service = spy(new ExampleJobService());

        HandlerThread ht = new HandlerThread("handler");
        ht.start();
        Handler h = new Handler(ht.getLooper());

        Intent executeJobIntent = new Intent(JobService.ACTION_EXECUTE);

        Job jobSpec = TestUtil.getBuilderWithNoopValidator()
            .setTag("tag")
            .setService(ExampleJobService.class)
            .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
            .setTrigger(Trigger.NOW)
            .setLifetime(Lifetime.FOREVER)
            .build();

        countDownLatch = new CountDownLatch(1);

        ((JobService.LocalBinder) service.onBind(executeJobIntent))
            .getService()
            .start(jobSpec, h.obtainMessage(ExecutionDelegator.JOB_FINISHED, jobSpec));

        assertTrue("Expected job to run to completion", countDownLatch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testOnStartCommand_handlesStartJob_doNotStartRunningJobAgain() {
        StoppableJobService service = new StoppableJobService(false);

        Job jobSpec = TestUtil.getBuilderWithNoopValidator()
                .setTag("tag")
                .setService(StoppableJobService.class)
                .setTrigger(Trigger.NOW)
                .build();

        ((JobService.LocalBinder) service.onBind(null)).getService().start(jobSpec, null);
        ((JobService.LocalBinder) service.onBind(null)).getService().start(jobSpec, null);

        assertEquals(1, service.getNumberOfExecutionRequestsReceived());
    }

    @Test
    public void stop_noCallback_finished() {
        JobService service = spy(new StoppableJobService(false));
        JobInvocation job = new Builder()
                .setTag("Tag")
                .setTrigger(Trigger.NOW)
                .setService(StoppableJobService.class.getName())
                .build();
        service.stop(job);
        verify(service, never()).onStopJob(job);
    }

    @Test
    public void stop_withCallback_retry() {
        JobService service = spy(new StoppableJobService(false));

        JobInvocation job = new Builder()
                .setTag("Tag")
                .setTrigger(Trigger.NOW)
                .setService(StoppableJobService.class.getName())
                .build();

        Handler handlerMock = mock(Handler.class);
        Message message = Message.obtain(handlerMock);
        service.start(job, message);

        service.stop(job);
        verify(service).onStopJob(job);
        verify(handlerMock).sendMessage(message);
        assertEquals(message.arg1, JobService.RESULT_SUCCESS);
    }

    @Test
    public void stop_withCallback_done() {
        JobService service = spy(new StoppableJobService(true));

        JobInvocation job = new Builder()
                .setTag("Tag")
                .setTrigger(Trigger.NOW)
                .setService(StoppableJobService.class.getName())
                .build();

        Handler handlerMock = mock(Handler.class);
        Message message = Message.obtain(handlerMock);
        service.start(job, message);

        service.stop(job);
        verify(service).onStopJob(job);
        verify(handlerMock).sendMessage(message);
        assertEquals(message.arg1, JobService.RESULT_FAIL_RETRY);
    }

    @Test
    public void onStartJob_jobFinishedReschedule() {
        // Verify that a retry request from within onStartJob will cause the retry result to be sent
        // to the bouncer service's handler, regardless of what value is ultimately returned from
        // onStartJob.
        JobService reschedulingService = new JobService() {
            @Override
            public boolean onStartJob(JobParameters job) {
                // Reschedules job.
                jobFinished(job, true /* retry this job */);
                return false;
            }

            @Override
            public boolean onStopJob(JobParameters job) {
                return false;
            }
        };

        Job jobSpec = TestUtil.getBuilderWithNoopValidator()
                .setTag("tag")
                .setService(reschedulingService.getClass())
                .setTrigger(Trigger.NOW)
                .build();
        Handler mock = mock(Handler.class);
        Message message = new Message();
        message.setTarget(mock);
        reschedulingService.start(jobSpec, message);

        verify(mock).sendMessage(message);
        assertEquals(message.arg1, JobService.RESULT_FAIL_RETRY);
    }

    @Test
    public void onStartJob_jobFinishedNotReschedule() {
        // Verify that a termination request from within onStartJob will cause the result to be sent
        // to the bouncer service's handler, regardless of what value is ultimately returned from
        // onStartJob.
        JobService reschedulingService = new JobService() {
            @Override
            public boolean onStartJob(JobParameters job) {
                jobFinished(job, false /* don't retry this job */);
                return false;
            }

            @Override
            public boolean onStopJob(JobParameters job) {
                return false;
            }
        };

        Job jobSpec = TestUtil.getBuilderWithNoopValidator()
                .setTag("tag")
                .setService(reschedulingService.getClass())
                .setTrigger(Trigger.NOW)
                .build();
        Handler mock = mock(Handler.class);
        Message message = new Message();
        message.setTarget(mock);
        reschedulingService.start(jobSpec, message);

        verify(mock).sendMessage(message);
        assertEquals(message.arg1, JobService.RESULT_SUCCESS);
    }

    public static class ExampleJobService extends JobService {
        @Override
        public boolean onStartJob(JobParameters job) {
            countDownLatch.countDown();
            return false;
        }

        @Override
        public boolean onStopJob(JobParameters job) {
            return false;
        }
    }

    public static class StoppableJobService extends JobService {

        private final boolean shouldReschedule;

        public int getNumberOfExecutionRequestsReceived() {
            return amountOfExecutionRequestReceived.get();
        }

        private final AtomicInteger amountOfExecutionRequestReceived = new AtomicInteger();

        public StoppableJobService(boolean shouldReschedule) {
            this.shouldReschedule = shouldReschedule;
        }

        @Override
        public boolean onStartJob(JobParameters job) {
            amountOfExecutionRequestReceived.incrementAndGet();
            return true;
        }

        @Override
        public boolean onStopJob(JobParameters job) {
            return shouldReschedule;
        }


    }
}
