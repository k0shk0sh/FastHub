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
 * RetryStrategy represents an approach to handling job execution failures. Jobs will have a
 * time-based backoff enforced, based on the chosen policy (one of {@code RETRY_POLICY_EXPONENTIAL}
 * or {@code RETRY_POLICY_LINEAR}.
 */
public final class RetryStrategy {
    /**
     * Increase the backoff time exponentially.
     * <p>
     * Calculated using {@code initial_backoff * 2 ^ (num_failures - 1)}.
     */
    public final static int RETRY_POLICY_EXPONENTIAL = 1;

    /**
     * Increase the backoff time linearly.
     * <p>
     * Calculated using {@code initial_backoff * num_failures}.
     */
    public final static int RETRY_POLICY_LINEAR = 2;

    /**
     * Expected schedule is: [30s, 60s, 120s, 240s, ..., 3600s]
     */
    public final static RetryStrategy DEFAULT_EXPONENTIAL =
        new RetryStrategy(RETRY_POLICY_EXPONENTIAL, 30, 3600);

    /**
     * Expected schedule is: [30s, 60s, 90s, 120s, ..., 3600s]
     */
    public final static RetryStrategy DEFAULT_LINEAR =
        new RetryStrategy(RETRY_POLICY_LINEAR, 30, 3600);

    @RetryPolicy
    private final int mPolicy;
    private final int mInitialBackoff;
    private final int mMaximumBackoff;

    /* package */ RetryStrategy(@RetryPolicy int policy, int initialBackoff, int maximumBackoff) {
        mPolicy = policy;
        mInitialBackoff = initialBackoff;
        mMaximumBackoff = maximumBackoff;
    }

    /**
     * Returns the backoff policy in place.
     */
    @RetryPolicy
    public int getPolicy() {
        return mPolicy;
    }

    /**
     * Returns the initial backoff (i.e. when # of failures == 1), in seconds.
     */
    public int getInitialBackoff() {
        return mInitialBackoff;
    }

    /**
     * Returns the maximum backoff duration in seconds.
     */
    public int getMaximumBackoff() {
        return mMaximumBackoff;
    }

    @IntDef({RETRY_POLICY_LINEAR, RETRY_POLICY_EXPONENTIAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RetryPolicy {
    }

    /* package */ final static class Builder {
        private final ValidationEnforcer mValidator;

        Builder(ValidationEnforcer validator) {
            mValidator = validator;
        }

        public RetryStrategy build(@RetryPolicy int policy, int initialBackoff, int maxBackoff) {
            RetryStrategy rs = new RetryStrategy(policy, initialBackoff, maxBackoff);
            mValidator.ensureValid(rs);
            return rs;
        }
    }
}
