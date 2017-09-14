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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;
import com.firebase.jobdispatcher.JobInvocation.Builder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE, sdk = 23)
public class JobInvocationTest {
    private Builder builder;

    @Before
    public void setUp() {
        builder = new Builder()
                .setTag("tag")
                .setService(TestJobService.class.getName())
                .setTrigger(Trigger.NOW);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testShouldReplaceCurrent() throws Exception {
        assertTrue("Expected shouldReplaceCurrent() to return value passed in constructor",
            builder.setReplaceCurrent(true).build().shouldReplaceCurrent());
        assertFalse("Expected shouldReplaceCurrent() to return value passed in constructor",
            builder.setReplaceCurrent(false).build().shouldReplaceCurrent());
    }

    @Test
    public void extras() throws Exception {
        assertNotNull(builder.build().getExtras());

        Bundle bundle = new Bundle();
        bundle.putLong("test", 1L);
        Bundle extras = builder.addExtras(bundle).build().getExtras();
        assertEquals(1, extras.size());
        assertEquals(1L, extras.getLong("test"));
    }

    @Test
    public void contract_hashCode_equals() {
        JobInvocation jobInvocation = builder.build();
        assertEquals(jobInvocation, builder.build());
        assertEquals(jobInvocation.hashCode(), builder.build().hashCode());
        JobInvocation jobInvocationNew = builder.setTag("new").build();
        assertNotEquals(jobInvocation, jobInvocationNew);
        assertNotEquals(jobInvocation.hashCode(), jobInvocationNew.hashCode());
    }

    @Test
    public void contract_hashCode_equals_triggerShouldBeIgnored() {
        JobInvocation jobInvocation = builder.build();
        JobInvocation periodic = builder.setTrigger(Trigger.executionWindow(0, 1)).build();
        assertEquals(jobInvocation, periodic);
        assertEquals(jobInvocation.hashCode(), periodic.hashCode());
    }
}
