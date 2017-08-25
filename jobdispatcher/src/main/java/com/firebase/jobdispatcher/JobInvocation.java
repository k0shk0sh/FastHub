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
import com.firebase.jobdispatcher.Constraint.JobConstraint;

/**
 * An internal non-Job implementation of JobParameters. Passed to JobService invocations.
 */
/* package */ final class JobInvocation implements JobParameters {

    @NonNull
    private final String mTag;

    @NonNull
    private final String mService;

    @NonNull
    private final JobTrigger mTrigger;

    private final boolean mRecurring;

    private final int mLifetime;

    @NonNull
    @JobConstraint
    private final int[] mConstraints;

    @NonNull
    private final Bundle mExtras;

    private final RetryStrategy mRetryStrategy;

    private final boolean mReplaceCurrent;

    private final TriggerReason mTriggerReason;

    private JobInvocation(Builder builder) {
        mTag = builder.mTag;
        mService = builder.mService;
        mTrigger = builder.mTrigger;
        mRetryStrategy = builder.mRetryStrategy;
        mRecurring = builder.mRecurring;
        mLifetime = builder.mLifetime;
        mConstraints = builder.mConstraints;
        mExtras = builder.mExtras;
        mReplaceCurrent = builder.mReplaceCurrent;
        mTriggerReason = builder.mTriggerReason;
    }

    @NonNull
    @Override
    public String getService() {
        return mService;
    }

    @NonNull
    @Override
    public String getTag() {
        return mTag;
    }

    @NonNull
    @Override
    public JobTrigger getTrigger() {
        return mTrigger;
    }

    @Override
    public int getLifetime() {
        return mLifetime;
    }

    @Override
    public boolean isRecurring() {
        return mRecurring;
    }

    @NonNull
    @Override
    public int[] getConstraints() {
        return mConstraints;
    }

    @NonNull
    @Override
    public Bundle getExtras() {
        return mExtras;
    }

    @NonNull
    @Override
    public RetryStrategy getRetryStrategy() {
        return mRetryStrategy;
    }

    @Override
    public boolean shouldReplaceCurrent() {
        return mReplaceCurrent;
    }

    @Override
    public TriggerReason getTriggerReason() {
        return mTriggerReason;
    }

    static final class Builder {

        @NonNull
        private String mTag;

        @NonNull
        private String mService;

        @NonNull
        private JobTrigger mTrigger;

        private boolean mRecurring;

        private int mLifetime;

        @NonNull
        @JobConstraint
        private int[] mConstraints;

        @NonNull
        private final Bundle mExtras = new Bundle();

        private RetryStrategy mRetryStrategy;

        private boolean mReplaceCurrent;

        private TriggerReason mTriggerReason;

        JobInvocation build() {
            if (mTag == null || mService == null || mTrigger == null) {
                throw new IllegalArgumentException("Required fields were not populated.");
            }
            return new JobInvocation(this);
        }

        public Builder setTag(@NonNull String mTag) {
            this.mTag = mTag;
            return this;
        }

        public Builder setService(@NonNull String mService) {
            this.mService = mService;
            return this;
        }

        public Builder setTrigger(@NonNull JobTrigger mTrigger) {
            this.mTrigger = mTrigger;
            return this;
        }

        public Builder setRecurring(boolean mRecurring) {
            this.mRecurring = mRecurring;
            return this;
        }

        public Builder setLifetime(@Lifetime.LifetimeConstant int mLifetime) {
            this.mLifetime = mLifetime;
            return this;
        }

        public Builder setConstraints(@JobConstraint @NonNull int[] mConstraints) {
            this.mConstraints = mConstraints;
            return this;
        }

        public Builder addExtras(@NonNull Bundle bundle) {
            if (bundle != null) {
                mExtras.putAll(bundle);
            }
            return this;
        }

        public Builder setRetryStrategy(RetryStrategy mRetryStrategy) {
            this.mRetryStrategy = mRetryStrategy;
            return this;
        }

        public Builder setReplaceCurrent(boolean mReplaceCurrent) {
            this.mReplaceCurrent = mReplaceCurrent;
            return this;
        }

        public Builder setTriggerReason(TriggerReason triggerReason) {
            this.mTriggerReason = triggerReason;
            return this;
        }
    }

    /**
     * @return true if the tag and the service of provided {@link JobInvocation} have the same
     * values.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().equals(o.getClass())) {
            return false;
        }

        JobInvocation jobInvocation = (JobInvocation) o;

        return mTag.equals(jobInvocation.mTag)
                && mService.equals(jobInvocation.mService);
    }

    @Override
    public int hashCode() {
        int result = mTag.hashCode();
        result = 31 * result + mService.hashCode();
        return result;
    }
}
