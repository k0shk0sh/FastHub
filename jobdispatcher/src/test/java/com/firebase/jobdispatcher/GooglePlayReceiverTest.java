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

import static com.firebase.jobdispatcher.TestUtil.encodeContentUriJob;
import static com.firebase.jobdispatcher.TestUtil.encodeRecurringContentUriJob;
import static com.firebase.jobdispatcher.TestUtil.getContentUriTrigger;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Parcel;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import com.firebase.jobdispatcher.GooglePlayReceiverTest.ShadowMessenger;
import com.firebase.jobdispatcher.JobInvocation.Builder;
import com.firebase.jobdispatcher.TestUtil.InspectableBinder;
import com.google.android.gms.gcm.PendingCallback;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implements;

@RunWith(RobolectricTestRunner.class)
@Config(
    constants = BuildConfig.class,
    manifest = Config.NONE,
    sdk = 21,
    shadows = {ShadowMessenger.class}
)
public class GooglePlayReceiverTest {

    /**
     * The default ShadowMessenger implementation causes NPEs when using the
     * {@link Messenger#Messenger(Handler)} constructor. We create our own empty Shadow so we can
     * just use the standard Android implementation, which is totally fine.
     *
     * @see <a href="https://github.com/robolectric/robolectric/issues/2246">Robolectric issue</a>
     *
     */
    @Implements(Messenger.class)
    public static class ShadowMessenger {}

    GooglePlayReceiver receiver;

    JobCoder jobCoder = new JobCoder(BundleProtocol.PACKED_PARAM_BUNDLE_PREFIX, true);

    @Mock
    Messenger messengerMock;
    @Mock
    IBinder binderMock;
    @Mock
    JobCallback callbackMock;
    @Mock
    ExecutionDelegator executionDelegatorMock;
    @Mock
    Driver driverMock;
    @Captor
    ArgumentCaptor<Job> jobArgumentCaptor;

    ArrayList<Uri> triggeredUris = new ArrayList<>();

    {
        triggeredUris.add(ContactsContract.AUTHORITY_URI);
        triggeredUris.add(Media.EXTERNAL_CONTENT_URI);
    }

    Builder jobInvocationBuilder = new Builder()
        .setTag("tag")
        .setService(TestJobService.class.getName())
        .setTrigger(Trigger.NOW);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        receiver = spy(new GooglePlayReceiver());
        when(receiver.getExecutionDelegator()).thenReturn(executionDelegatorMock);
        receiver.driver = driverMock;
        receiver.validationEnforcer = new ValidationEnforcer(new NoopJobValidator());
    }

    @Test
    public void onJobFinished_unknownJobCallbackIsNotPresent_ignoreNoException() {
        receiver.onJobFinished(jobInvocationBuilder.build(), JobService.RESULT_SUCCESS);

        verifyZeroInteractions(driverMock);
    }

    @Test
    public void onJobFinished_notRecurringContentJob_sendResult() {
        jobInvocationBuilder.setTrigger(
            Trigger.contentUriTrigger(Arrays.asList(new ObservedUri(Contacts.CONTENT_URI, 0))));

        JobInvocation jobInvocation = receiver
            .prepareJob(callbackMock, getBundleForContentJobExecution());

        receiver.onJobFinished(jobInvocation, JobService.RESULT_SUCCESS);
        verify(callbackMock).jobFinished(JobService.RESULT_SUCCESS);
        verifyZeroInteractions(driverMock);
    }

    @Test
    public void onJobFinished_successRecurringContentJob_reschedule() {
        JobInvocation jobInvocation = receiver
            .prepareJob(callbackMock, getBundleForContentJobExecutionRecurring());

        receiver.onJobFinished(jobInvocation, JobService.RESULT_SUCCESS);

        verify(driverMock).schedule(jobArgumentCaptor.capture());

        // No need to callback when job finished.
        // Reschedule request is treated as two events: completion of old job and scheduling of new
        // job with the same parameters.
        verifyZeroInteractions(callbackMock);

        Job rescheduledJob = jobArgumentCaptor.getValue();
        TestUtil.assertJobsEqual(jobInvocation, rescheduledJob);
    }

    @Test
    public void onJobFinished_failWithRetryRecurringContentJob_sendResult() {
        JobInvocation jobInvocation = receiver
            .prepareJob(callbackMock, getBundleForContentJobExecutionRecurring());

        receiver.onJobFinished(jobInvocation, JobService.RESULT_FAIL_RETRY);

        // If a job finishes with RESULT_FAIL_RETRY we don't need to send a reschedule request.
        // Rescheduling will erase previously triggered URIs.
        verify(callbackMock).jobFinished(JobService.RESULT_FAIL_RETRY);

        verifyZeroInteractions(driverMock);
    }

    @Test
    public void prepareJob() {
        Intent intent = new Intent();

        Bundle encode = encodeContentUriJob(getContentUriTrigger(), jobCoder);
        intent.putExtra(GooglePlayJobWriter.REQUEST_PARAM_EXTRAS, encode);

        Parcel container = Parcel.obtain();
        container.writeStrongBinder(new Binder());
        PendingCallback pcb = new PendingCallback(container);
        intent.putExtra("callback", pcb);

        ArrayList<Uri> uris = new ArrayList<>();
        uris.add(ContactsContract.AUTHORITY_URI);
        uris.add(Media.EXTERNAL_CONTENT_URI);
        intent.putParcelableArrayListExtra(BundleProtocol.PACKED_PARAM_TRIGGERED_URIS, uris);

        JobInvocation jobInvocation = receiver.prepareJob(intent);
        assertEquals(jobInvocation.getTriggerReason().getTriggeredContentUris(), uris);
    }

    @Test
    public void prepareJob_messenger() {
        JobInvocation jobInvocation = receiver.prepareJob(callbackMock, new Bundle());
        assertNull(jobInvocation);
        verify(callbackMock).jobFinished(JobService.RESULT_FAIL_NORETRY);
    }

    @Test
    public void prepareJob_messenger_noExtras() {
        Bundle bundle = getBundleForContentJobExecution();

        JobInvocation jobInvocation = receiver.prepareJob(callbackMock, bundle);
        assertEquals(jobInvocation.getTriggerReason().getTriggeredContentUris(), triggeredUris);
    }

    @NonNull
    private Bundle getBundleForContentJobExecution() {
        Bundle bundle = new Bundle();

        Bundle encode = encodeContentUriJob(getContentUriTrigger(), jobCoder);
        bundle.putBundle(GooglePlayJobWriter.REQUEST_PARAM_EXTRAS, encode);

        bundle.putParcelableArrayList(BundleProtocol.PACKED_PARAM_TRIGGERED_URIS, triggeredUris);
        return bundle;
    }

    @NonNull
    private Bundle getBundleForContentJobExecutionRecurring() {
        Bundle bundle = new Bundle();

        Bundle encode = encodeRecurringContentUriJob(getContentUriTrigger(), jobCoder);
        bundle.putBundle(GooglePlayJobWriter.REQUEST_PARAM_EXTRAS, encode);

        bundle.putParcelableArrayList(BundleProtocol.PACKED_PARAM_TRIGGERED_URIS, triggeredUris);
        return bundle;
    }

    @Test
    public void onBind() {
        Intent intent = new Intent(GooglePlayReceiver.ACTION_EXECUTE);
        IBinder binderA = receiver.onBind(intent);
        IBinder binderB = receiver.onBind(intent);

        assertEquals(binderA, binderB);
    }

    @Test
    public void onBind_nullIntent() {
        IBinder binder = receiver.onBind(null);
        assertNull(binder);
    }

    @Test
    public void onBind_wrongAction() {
        Intent intent = new Intent("test");
        IBinder binder = receiver.onBind(intent);
        assertNull(binder);
    }

    @Test
    @Config(sdk = VERSION_CODES.KITKAT)
    public void onBind_wrongBuild() {
        Intent intent = new Intent(GooglePlayReceiver.ACTION_EXECUTE);
        IBinder binder = receiver.onBind(intent);
        assertNull(binder);
    }

    @Test
    public void onStartCommand_nullIntent() {
        assertResultWasStartNotSticky(receiver.onStartCommand(null, 0, 101));
        verify(receiver).stopSelf(101);
    }

    @Test
    public void onStartCommand_initAction() {
        Intent initIntent = new Intent("com.google.android.gms.gcm.SERVICE_ACTION_INITIALIZE");
        assertResultWasStartNotSticky(receiver.onStartCommand(initIntent, 0, 101));
        verify(receiver).stopSelf(101);
    }

    @Test
    public void onStartCommand_unknownAction() {
        Intent unknownIntent = new Intent("com.example.foo.bar");
        assertResultWasStartNotSticky(receiver.onStartCommand(unknownIntent, 0, 101));
        assertResultWasStartNotSticky(receiver.onStartCommand(unknownIntent, 0, 102));
        assertResultWasStartNotSticky(receiver.onStartCommand(unknownIntent, 0, 103));

        InOrder inOrder = inOrder(receiver);
        inOrder.verify(receiver).stopSelf(101);
        inOrder.verify(receiver).stopSelf(102);
        inOrder.verify(receiver).stopSelf(103);
    }

    @Test
    public void onStartCommand_executeActionWithEmptyExtras() {
        Intent execIntent = new Intent("com.google.android.gms.gcm.ACTION_TASK_READY");
        assertResultWasStartNotSticky(receiver.onStartCommand(execIntent, 0, 101));
        verify(receiver).stopSelf(101);
    }

    @Test
    public void onStartCommand_executeAction() {
        JobInvocation job = new JobInvocation.Builder()
            .setTag("tag")
            .setService("com.example.foo.FooService")
            .setTrigger(Trigger.NOW)
            .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
            .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
            .setConstraints(new int[]{Constraint.DEVICE_IDLE})
            .build();

        Intent execIntent = new Intent("com.google.android.gms.gcm.ACTION_TASK_READY")
            .putExtra("extras", new JobCoder(BundleProtocol.PACKED_PARAM_BUNDLE_PREFIX, true)
                .encode(job, new Bundle()))
            .putExtra("callback", new InspectableBinder().toPendingCallback());

        when(executionDelegatorMock.executeJob(any(JobInvocation.class))).thenReturn(true);

        assertResultWasStartNotSticky(receiver.onStartCommand(execIntent, 0, 101));

        verify(receiver, never()).stopSelf(anyInt());
        verify(executionDelegatorMock).executeJob(any(JobInvocation.class));

        receiver.onJobFinished(job, JobService.RESULT_SUCCESS);

        verify(receiver).stopSelf(101);
    }

    private void assertResultWasStartNotSticky(int result) {
        assertEquals(
            "Result for onStartCommand wasn't START_NOT_STICKY", Service.START_NOT_STICKY, result);
    }
}
