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

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Lifetime represents how long a Job should last.
 */
public final class Lifetime {
    /**
     * The Job should be preserved until the next boot. This is the default.
     */
    public final static int UNTIL_NEXT_BOOT = 1;

    /**
     * The Job should be preserved "forever."
     */
    public final static int FOREVER = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FOREVER, UNTIL_NEXT_BOOT})
    @interface LifetimeConstant {}
}
