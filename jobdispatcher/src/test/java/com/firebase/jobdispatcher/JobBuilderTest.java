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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE, sdk = 23)
public class JobBuilderTest {
    private static final int[] ALL_LIFETIMES = {Lifetime.UNTIL_NEXT_BOOT, Lifetime.FOREVER};

    private Job.Builder mBuilder;

    @Before
    public void setUp() throws Exception {
        mBuilder = TestUtil.getBuilderWithNoopValidator();
    }

    @Test
    public void testAddConstraints() {
        mBuilder.setConstraints()
            .addConstraint(Constraint.DEVICE_CHARGING)
            .addConstraint(Constraint.ON_UNMETERED_NETWORK);

        int[] expected = {Constraint.DEVICE_CHARGING, Constraint.ON_UNMETERED_NETWORK};

        assertEquals(Constraint.compact(expected), Constraint.compact(mBuilder.getConstraints()));
    }

    @Test
    public void testSetLifetime() {
        for (int lifetime : ALL_LIFETIMES) {
            mBuilder.setLifetime(lifetime);
            assertEquals(lifetime, mBuilder.getLifetime());
        }
    }

    @Test
    public void testSetShouldReplaceCurrent() {
        for (boolean replace : new boolean[]{true, false}) {
            mBuilder.setReplaceCurrent(replace);
            assertEquals(replace, mBuilder.shouldReplaceCurrent());
        }
    }
}
