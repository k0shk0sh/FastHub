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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.firebase.jobdispatcher.Constraint.JobConstraint;

/**
 * JobParameters represents anything that can describe itself in terms of Job components.
 */
public interface JobParameters {

    /**
     * Returns the name of the backing JobService class.
     */
    @NonNull
    String getService();

    /**
     * Returns a string identifier for the Job. Used when cancelling Jobs and displaying debug
     * messages.
     */
    @NonNull
    String getTag();

    /**
     * The Job's Trigger, which decides when the Job is ready to run.
     */
    @NonNull
    JobTrigger getTrigger();

    /**
     * The Job's lifetime; how long it should persist for.
     */
    @Lifetime.LifetimeConstant
    int getLifetime();

    /**
     * Whether the Job should repeat.
     */
    boolean isRecurring();

    /**
     * The runtime constraints applied to this Job. A Job is not run until the trigger is activated
     * and all the runtime constraints are satisfied.
     */
    @JobConstraint
    int[] getConstraints();

    /**
     * The optional set of user-supplied extras associated with this Job.
     */
    @Nullable
    Bundle getExtras();

    /**
     * The RetryStrategy for the Job. Used to determine how to handle failures.
     */
    @NonNull
    RetryStrategy getRetryStrategy();

    /**
     * Whether the Job should replace a pre-existing Job with the same tag.
     */
    boolean shouldReplaceCurrent();

    /** @return A {@link TriggerReason} that - if non null - describes why the job was triggered. */
    @Nullable
    TriggerReason getTriggerReason();
}
