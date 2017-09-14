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

import android.support.annotation.Nullable;
import java.util.List;

/**
 * A JobValidator is an object that knows how to validate Jobs and some of their composite
 * components.
 */
public interface JobValidator {
    /**
     * Returns a List of error messages, or null if the JobParameters is
     * valid.
     */
    @Nullable
    List<String> validate(JobParameters job);

    /**
     * Returns a List of error messages, or null if the Trigger is
     * valid.
     * @param trigger
     */
    @Nullable
    List<String> validate(JobTrigger trigger);

    /**
     * Returns a List of error messages, or null if the RetryStrategy
     * is valid.
     */
    @Nullable
    List<String> validate(RetryStrategy retryStrategy);

}
