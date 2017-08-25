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
 * A very simple Validator that thinks that everything is ok. Used for testing.
 */
class NoopJobValidator implements JobValidator {

    @Nullable
    @Override
    public List<String> validate(JobParameters job) {
        return null;
    }

    @Nullable
    @Override
    public List<String> validate(JobTrigger trigger) {
        return null;
    }

    @Nullable
    @Override
    public List<String> validate(RetryStrategy retryStrategy) {
        return null;
    }
}
