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
import android.support.annotation.NonNull;
import com.firebase.jobdispatcher.RetryStrategy.RetryPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The FirebaseJobDispatcher provides a driver-agnostic API for scheduling and cancelling Jobs.
 *
 * @see #FirebaseJobDispatcher(Driver)
 * @see Driver
 * @see JobParameters
 */
public final class FirebaseJobDispatcher {
    /**
     * Indicates the schedule request seems to have been successful.
     */
    public final static int SCHEDULE_RESULT_SUCCESS = 0;

    /**
     * Indicates the schedule request encountered an unknown error.
     */
    public final static int SCHEDULE_RESULT_UNKNOWN_ERROR = 1;

    /**
     * Indicates the schedule request failed because the driver was unavailable.
     */
    public final static int SCHEDULE_RESULT_NO_DRIVER_AVAILABLE = 2;

    /**
     * Indicates the schedule request failed because the Trigger was unsupported.
     */
    public final static int SCHEDULE_RESULT_UNSUPPORTED_TRIGGER = 3;

    /**
     * Indicates the schedule request failed because the service is not exposed or configured
     * correctly.
     */
    public final static int SCHEDULE_RESULT_BAD_SERVICE = 4;

    /**
     * Indicates the cancel request seems to have been successful.
     */
    public final static int CANCEL_RESULT_SUCCESS = 0;
    /**
     * Indicates the cancel request encountered an unknown error.
     */
    public final static int CANCEL_RESULT_UNKNOWN_ERROR = 1;
    /**
     * Indicates the cancel request failed because the driver was unavailable.
     */
    public final static int CANCEL_RESULT_NO_DRIVER_AVAILABLE = 2;
    /**
     * The backing Driver for this instance.
     */
    private final Driver mDriver;
    /**
     * The ValidationEnforcer configured for the current Driver.
     */
    private final ValidationEnforcer mValidator;
    /**
     * Single instance of a RetryStrategy.Builder, configured with the current driver's validation
     * settings. We can do this because the RetryStrategy.Builder is stateless.
     */
    private RetryStrategy.Builder mRetryStrategyBuilder;

    /**
     * Instantiates a new FirebaseJobDispatcher using the provided Driver.
     */
    public FirebaseJobDispatcher(Driver driver) {
        mDriver = driver;
        mValidator = new ValidationEnforcer(mDriver.getValidator());
        mRetryStrategyBuilder = new RetryStrategy.Builder(mValidator);
    }

    /**
     * Attempts to schedule the provided Job.
     * <p>
     * Returns one of the SCHEDULE_RESULT_ constants.
     */
    @ScheduleResult
    public int schedule(@NonNull Job job) {
        if (!mDriver.isAvailable()) {
            return SCHEDULE_RESULT_NO_DRIVER_AVAILABLE;
        }

        return mDriver.schedule(job);
    }

    /**
     * Attempts to cancel the Job that matches the provided tag and endpoint.
     * <p>
     * Returns one of the CANCEL_RESULT_ constants.
     */
    @CancelResult
    public int cancel(@NonNull String tag) {
        if (!mDriver.isAvailable()) {
            return CANCEL_RESULT_NO_DRIVER_AVAILABLE;
        }

        return mDriver.cancel(tag);
    }

    /**
     * Attempts to cancel all Jobs registered for this package.
     * <p>
     * Returns one of the CANCEL_RESULT_ constants.
     */
    @CancelResult
    public int cancelAll() {
        if (!mDriver.isAvailable()) {
            return CANCEL_RESULT_NO_DRIVER_AVAILABLE;
        }

        return mDriver.cancelAll();
    }

    /**
     * Attempts to schedule the provided Job, throwing an exception if it fails.
     *
     * @throws ScheduleFailedException
     */
    public void mustSchedule(Job job) {
        if (schedule(job) != SCHEDULE_RESULT_SUCCESS) {
            throw new ScheduleFailedException();
        }
    }

    /**
     * Returns a ValidationEnforcer configured for the current Driver.
     */
    public ValidationEnforcer getValidator() {
        return mValidator;
    }

    /**
     * Creates a new Job.Builder, configured with the current driver's validation settings.
     */
    @NonNull
    public Job.Builder newJobBuilder() {
        return new Job.Builder(mValidator);
    }

    /**
     * Creates a new RetryStrategy from the provided parameters, validated with the current driver's
     * {@link JobValidator}.
     *
     * @param policy         the backoff policy to use. One of the {@link RetryPolicy} constants.
     * @param initialBackoff the initial backoff, in seconds.
     * @param maximumBackoff the maximum backoff, in seconds.
     * @throws ValidationEnforcer.ValidationException
     * @see RetryStrategy
     */
    public RetryStrategy newRetryStrategy(@RetryPolicy int policy, int initialBackoff,
                                          int maximumBackoff) {

        return mRetryStrategyBuilder.build(policy, initialBackoff, maximumBackoff);
    }

    /**
     * Results that can legally be returned from {@link #schedule(Job)} calls.
     */
    @IntDef({
        SCHEDULE_RESULT_SUCCESS,
        SCHEDULE_RESULT_UNKNOWN_ERROR,
        SCHEDULE_RESULT_NO_DRIVER_AVAILABLE,
        SCHEDULE_RESULT_UNSUPPORTED_TRIGGER,
        SCHEDULE_RESULT_BAD_SERVICE,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScheduleResult {
    }

    /**
     * Results that can legally be returned from {@link #cancel(String)} or {@link #cancelAll()}
     * calls.
     */
    @IntDef({
        CANCEL_RESULT_SUCCESS,
        CANCEL_RESULT_UNKNOWN_ERROR,
        CANCEL_RESULT_NO_DRIVER_AVAILABLE,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface CancelResult {
    }

    /**
     * Thrown when a {@link FirebaseJobDispatcher#schedule(com.firebase.jobdispatcher.Job)} call
     * fails.
     */
    public final static class ScheduleFailedException extends RuntimeException {
    }
}
