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
 * Job is the embodiment of a unit of work and an associated set of triggers, settings, and runtime
 * constraints.
 */
public final class Job implements JobParameters {
    private final String mService;
    private final String mTag;
    private final JobTrigger mTrigger;
    private final RetryStrategy mRetryStrategy;
    private final int mLifetime;
    private final boolean mRecurring;
    private final int[] mConstraints;
    private final boolean mReplaceCurrent;
    private Bundle mExtras;

    private Job(Builder builder) {
        mService = builder.mServiceClassName;
        mExtras = builder.mExtras;
        mTag = builder.mTag;
        mTrigger = builder.mTrigger;
        mRetryStrategy = builder.mRetryStrategy;
        mLifetime = builder.mLifetime;
        mRecurring = builder.mRecurring;
        mConstraints = builder.mConstraints != null ? builder.mConstraints : new int[0];
        mReplaceCurrent = builder.mReplaceCurrent;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public int[] getConstraints() {
        return mConstraints;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Bundle getExtras() {
        return mExtras;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public RetryStrategy getRetryStrategy() {
        return mRetryStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldReplaceCurrent() {
        return mReplaceCurrent;
    }

    @Nullable
    @Override
    public TriggerReason getTriggerReason() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public String getTag() {
        return mTag;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public JobTrigger getTrigger() {
        return mTrigger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLifetime() {
        return mLifetime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRecurring() {
        return mRecurring;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public String getService() {
        return mService;
    }

    /**
     * A class that understands how to build a {@link Job}. Retrieved by calling
     * {@link FirebaseJobDispatcher#newJobBuilder()}.
     */
    public final static class Builder implements JobParameters {
        private final ValidationEnforcer mValidator;

        private String mServiceClassName;
        private Bundle mExtras;
        private String mTag;
        private JobTrigger mTrigger = Trigger.NOW;
        private int mLifetime = Lifetime.UNTIL_NEXT_BOOT;
        private int[] mConstraints;

        private RetryStrategy mRetryStrategy = RetryStrategy.DEFAULT_EXPONENTIAL;
        private boolean mReplaceCurrent = false;
        private boolean mRecurring = false;

        Builder(ValidationEnforcer validator) {
            mValidator = validator;
        }

        Builder(ValidationEnforcer validator, JobParameters job) {
            mValidator = validator;

            mTag = job.getTag();
            mServiceClassName = job.getService();
            mTrigger = job.getTrigger();
            mRecurring = job.isRecurring();
            mLifetime = job.getLifetime();
            mConstraints = job.getConstraints();
            mExtras = job.getExtras();
            mRetryStrategy = job.getRetryStrategy();
        }

        /**
         * Adds the provided constraint to the current list of runtime constraints.
         */
        public Builder addConstraint(@JobConstraint int constraint) {
            // Create a new, longer constraints array
            int[] newConstraints = new int[mConstraints == null ? 1 : mConstraints.length + 1];

            if (mConstraints != null && mConstraints.length != 0) {
                // Copy all the old values over
                System.arraycopy(mConstraints, 0, newConstraints, 0, mConstraints.length);
            }

            // add the new value
            newConstraints[newConstraints.length - 1] = constraint;
            // update the pointer
            mConstraints = newConstraints;

            return this;
        }

        /**
         * Sets whether this Job should replace pre-existing Jobs with the same tag.
         */
        public Builder setReplaceCurrent(boolean replaceCurrent) {
            mReplaceCurrent = replaceCurrent;

            return this;
        }

        /**
         * Builds the Job, using the settings provided so far.
         *
         * @throws ValidationEnforcer.ValidationException
         */
        public Job build() {
            mValidator.ensureValid(this);

            return new Job(this);
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public String getService() {
            return mServiceClassName;
        }

        /**
         * Sets the backing JobService class for the Job. See {@link #getService()}.
         */
        public Builder setService(Class<? extends JobService> serviceClass) {
            mServiceClassName = serviceClass == null ? null : serviceClass.getName();

            return this;
        }

        /**
         * Sets the backing JobService class name for the Job. See {@link #getService()}.
         *
         * <p>Should not be exposed, for internal use only.
         */
        Builder setServiceName(String serviceClassName) {
            mServiceClassName = serviceClassName;

            return this;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public String getTag() {
            return mTag;
        }

        /**
         * Sets the unique String tag used to identify the Job. See {@link #getTag()}.
         */
        public Builder setTag(String tag) {
            mTag = tag;

            return this;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public JobTrigger getTrigger() {
            return mTrigger;
        }

        /**
         * Sets the Trigger used for the Job. See {@link #getTrigger()}.
         */
        public Builder setTrigger(JobTrigger trigger) {
            mTrigger = trigger;

            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Lifetime.LifetimeConstant
        public int getLifetime() {
            return mLifetime;
        }

        /**
         * Sets the Job's lifetime, or how long it should persist. See {@link #getLifetime()}.
         */
        public Builder setLifetime(@Lifetime.LifetimeConstant int lifetime) {
            mLifetime = lifetime;

            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRecurring() {
            return mRecurring;
        }

        /**
         * Sets whether the job should recur. The default is false.
         */
        public Builder setRecurring(boolean recurring) {
            mRecurring = recurring;

            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @JobConstraint
        public int[] getConstraints() {
            return mConstraints == null ? new int[]{} : mConstraints;
        }

        /**
         * Sets the Job's runtime constraints. See {@link #getConstraints()}.
         */
        public Builder setConstraints(@JobConstraint int... constraints) {
            mConstraints = constraints;

            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nullable
        @Override
        public Bundle getExtras() {
            return mExtras;
        }

        /**
         * Sets the user-defined extras associated with the Job. See {@link #getExtras()}.
         */
        public Builder setExtras(Bundle extras) {
            mExtras = extras;

            return this;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public RetryStrategy getRetryStrategy() {
            return mRetryStrategy;
        }

        /**
         * Set the RetryStrategy used for the Job. See {@link #getRetryStrategy()}.
         */
        public Builder setRetryStrategy(RetryStrategy retryStrategy) {
            mRetryStrategy = retryStrategy;

            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean shouldReplaceCurrent() {
            return mReplaceCurrent;
        }

        @Nullable
        @Override
        public TriggerReason getTriggerReason() {
            return null;
        }
    }
}
