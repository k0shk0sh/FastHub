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
import com.firebase.jobdispatcher.FirebaseJobDispatcher.CancelResult;
import com.firebase.jobdispatcher.FirebaseJobDispatcher.ScheduleResult;

/**
 * Driver represents a component that understands how to schedule, validate, and execute jobs.
 */
public interface Driver {

    /**
     * Schedules the provided Job.
     *
     * @return one of the SCHEDULE_RESULT_ constants
     */
    @ScheduleResult
    int schedule(@NonNull Job job);

    /**
     * Cancels the job with the provided tag and class.
     *
     * @return one of the CANCEL_RESULT_ constants.
     */
    @CancelResult
    int cancel(@NonNull String tag);

    /**
     * Cancels all jobs registered with this Driver.
     *
     * @return one of the CANCEL_RESULT_ constants.
     */
    @CancelResult
    int cancelAll();

    /**
     * Returns a JobValidator configured for this backend.
     */
    @NonNull
    JobValidator getValidator();

    /**
     * Indicates whether the backend is available.
     */
    boolean isAvailable();
}
