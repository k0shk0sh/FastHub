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
import static org.junit.Assert.assertEquals;

import android.os.Message;
import android.os.Messenger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Tests {@link GooglePlayMessengerCallback}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE, sdk = 21)
public class GooglePlayMessengerCallbackTest {

    @Mock
    Messenger messengerMock;
    GooglePlayMessengerCallback callback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        callback = new GooglePlayMessengerCallback(messengerMock, "tag");
    }

    @Test
    public void jobFinished() throws Exception {
        final ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        callback.jobFinished(JobService.RESULT_SUCCESS);

        Mockito.verify(messengerMock).send(messageCaptor.capture());
        Message message = messageCaptor.getValue();
        assertEquals(message.what, GooglePlayMessageHandler.MSG_RESULT);
        assertEquals(message.arg1, JobService.RESULT_SUCCESS);
        assertEquals(message.getData().getString(REQUEST_PARAM_TAG), "tag");
    }
}
