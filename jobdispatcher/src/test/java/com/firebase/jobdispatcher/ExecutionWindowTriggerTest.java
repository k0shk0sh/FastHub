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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE, sdk = 23)
public class ExecutionWindowTriggerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testNewInstance_withValidWindow() throws Exception {
        JobTrigger.ExecutionWindowTrigger trigger = Trigger.executionWindow(0, 60);

        assertEquals(0, trigger.getWindowStart());
        assertEquals(60, trigger.getWindowEnd());
    }

    @Test
    public void testNewInstance_withNegativeStart() throws Exception {
        expectedException.expect(IllegalArgumentException.class);

        Trigger.executionWindow(-10, 60);
    }

    @Test
    public void testNewInstance_withNegativeEnd() throws Exception {
        expectedException.expect(IllegalArgumentException.class);

        Trigger.executionWindow(0, -1);
    }

    @Test
    public void testNewInstance_withReversedValues() throws Exception {
        expectedException.expect(IllegalArgumentException.class);

        Trigger.executionWindow(60, 0);
    }

    @Test
    public void testNewInstance_withTooSmallWindow_now() throws Exception {
        expectedException.expect(IllegalArgumentException.class);

        Trigger.executionWindow(60, 59);
    }

    @Test
    public void testNewInstance_withTooSmallWindow_inFuture() throws Exception {
        expectedException.expect(IllegalArgumentException.class);

        Trigger.executionWindow(200, 100);
    }
}
