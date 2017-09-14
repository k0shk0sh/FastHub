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

import android.support.annotation.NonNull;
import java.util.List;

/**
 * Generally, a Trigger is an object that can answer the question, "is this job ready to run?"
 * <p>
 * More specifically, a Trigger is an opaque, abstract class used to root the type hierarchy.
 */
public final class Trigger {

    /**
     * Immediate is a Trigger that's immediately available. The Job will be run as soon as the
     * runtime constraints are satisfied.
     * <p>
     * It is invalid to schedule an Immediate with a recurring Job.
     */
    public final static JobTrigger.ImmediateTrigger NOW = new JobTrigger.ImmediateTrigger();

    /**
     * Creates a new ExecutionWindow based on the provided time interval.
     *
     * @param windowStart The earliest time (in seconds) the job should be
     *                    considered eligible to run. Calculated from when the
     *                    job was scheduled (for new jobs) or last run (for
     *                    recurring jobs).
     * @param windowEnd   The latest time (in seconds) the job should be run in
     *                    an ideal world. Calculated in the same way as
     *                    {@code windowStart}.
     * @throws IllegalArgumentException if the provided parameters are too
     *                                  restrictive.
     */
    public static JobTrigger.ExecutionWindowTrigger executionWindow(int windowStart, int windowEnd) {
        if (windowStart < 0) {
            throw new IllegalArgumentException("Window start can't be less than 0");
        } else if (windowEnd < windowStart) {
            throw new IllegalArgumentException("Window end can't be less than window start");
        }

        return new JobTrigger.ExecutionWindowTrigger(windowStart, windowEnd);
    }

    /**
     * Creates a new ContentUriTrigger based on the provided list of {@link ObservedUri}.
     *
     * @param uris The list of URIs to observe. The trigger will be available if a piece of content,
     *     corresponding to any of provided URIs, is updated.
     * @throws IllegalArgumentException if provided list of URIs is null or empty.
     */
    public static JobTrigger.ContentUriTrigger contentUriTrigger(@NonNull List<ObservedUri> uris) {
        if (uris == null || uris.isEmpty()) {
            throw new IllegalArgumentException("Uris must not be null or empty.");
        }
        return new JobTrigger.ContentUriTrigger(uris);
    }
}
