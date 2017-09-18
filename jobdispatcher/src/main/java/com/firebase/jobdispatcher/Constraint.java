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
import android.support.annotation.VisibleForTesting;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A Constraint is a runtime requirement for a job. A job only becomes eligible to run once its
 * trigger has been activated and all constraints are satisfied.
 */
public final class Constraint {
    /**
     * Only run the job when an unmetered network is available.
     */
    public static final int ON_UNMETERED_NETWORK = 1;

    /**
     * Only run the job when a network connection is available. If both this and
     * {@link #ON_UNMETERED_NETWORK} is provided, {@link #ON_UNMETERED_NETWORK} will take
     * precedence.
     */
    public static final int ON_ANY_NETWORK = 1 << 1;

    /**
     * Only run the job when the device is currently charging.
     */
    public static final int DEVICE_CHARGING = 1 << 2;

    /**
     * Only run the job when the device is idle. This is ignored for devices that don't expose the
     * concept of an idle state.
     */
    public static final int DEVICE_IDLE = 1 << 3;

    @VisibleForTesting
    static final int[] ALL_CONSTRAINTS = {
      ON_ANY_NETWORK, ON_UNMETERED_NETWORK, DEVICE_CHARGING, DEVICE_IDLE};

    /** Constraint shouldn't ever be instantiated. */
    private Constraint() {}

    /**
     * A tooling type-hint for any of the valid constraint values.
     */
    @IntDef(flag = true, value = {
        ON_ANY_NETWORK,
        ON_UNMETERED_NETWORK,
        DEVICE_CHARGING,
        DEVICE_IDLE,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface JobConstraint {}

    /**
     * Compact a provided array of constraints into a single int.
     *
     * @see #uncompact(int)
     */
    static int compact(@JobConstraint int[] constraints) {
        int result = 0;
        if (constraints == null) {
            return result;
        }
        for (int c : constraints) {
            result |= c;
        }
        return result;
    }

    /**
     * Unpack a single int into an array of constraints.
     *
     * @see #compact(int[])
     */
    static int[] uncompact(int compactConstraints) {
        int length = 0;
        for (int c : ALL_CONSTRAINTS) {
            length += (compactConstraints & c) == c ? 1 : 0;
        }
        int[] list = new int[length];

        int i = 0;
        for (int c : ALL_CONSTRAINTS) {
            if ((compactConstraints & c) == c) {
                list[i++] = c;
            }
        }

        return list;
    }
}
