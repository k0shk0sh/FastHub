// Copyright 2016 Google, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//            http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package com.firebase.jobdispatcher;

import static junit.framework.Assert.assertEquals;

import android.provider.ContactsContract;
import com.firebase.jobdispatcher.JobTrigger.ContentUriTrigger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** Test for {@link ContentUriTrigger}. */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE, sdk = 21)
public class ContentUriTriggerTest {

    @Test(expected = IllegalArgumentException.class)
    public void constrains_null() throws Exception {
        Trigger.contentUriTrigger(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constrains_emptyList() throws Exception {
        Trigger.contentUriTrigger(Collections.<ObservedUri>emptyList());
    }

    @Test
    public void constrains_valid() throws Exception {
        List<ObservedUri> uris = Arrays.asList(new ObservedUri(ContactsContract.AUTHORITY_URI, 0));
        ContentUriTrigger uriTrigger = Trigger.contentUriTrigger(uris);
        assertEquals(uris, uriTrigger.getUris());
    }
}
