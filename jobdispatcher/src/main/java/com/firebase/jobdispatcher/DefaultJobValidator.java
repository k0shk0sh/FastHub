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

import static com.firebase.jobdispatcher.RetryStrategy.RETRY_POLICY_EXPONENTIAL;
import static com.firebase.jobdispatcher.RetryStrategy.RETRY_POLICY_LINEAR;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Validates Jobs according to some safe standards.
 * <p>
 * Custom JobValidators should typically extend from this.
 */
public class DefaultJobValidator implements JobValidator {

    /**
     * The maximum length of a tag, in characters (i.e. String.length()). Strings longer than this
     * will cause validation to fail.
     */
    public static final int MAX_TAG_LENGTH = 100;

    /**
     * The maximum size, in bytes, that the provided extras bundle can be. Corresponds to
     * {@link Parcel#dataSize()}.
     */
    public final static int MAX_EXTRAS_SIZE_BYTES = 10 * 1024;

    /** Private ref to the Context. Necessary to check that the manifest is configured correctly. */
    private final Context context;

    public DefaultJobValidator(Context context) {
        this.context = context;
    }

    /** @see {@link #MAX_EXTRAS_SIZE_BYTES}. */
    private static int measureBundleSize(Bundle extras) {
        Parcel p = Parcel.obtain();
        extras.writeToParcel(p, 0);
        int sizeInBytes = p.dataSize();
        p.recycle();

        return sizeInBytes;
    }

    /** Combines two {@literal List<String>s} together. */
    @Nullable
    private static List<String> mergeErrorLists(@Nullable List<String> errors,
                                                @Nullable List<String> newErrors) {
        if (errors == null) {
            return newErrors;
        }
        if (newErrors == null) {
            return errors;
        }

        errors.addAll(newErrors);
        return errors;
    }

    @Nullable
    private static List<String> addError(@Nullable List<String> errors, String newError) {
        if (newError == null) {
            return errors;
        }
        if (errors == null) {
            return getMutableSingletonList(newError);
        }

        Collections.addAll(errors, newError);

        return errors;
    }

    @Nullable
    private static List<String> addErrorsIf(boolean condition, List<String> errors, String newErr) {
        if (condition) {
            return addError(errors, newErr);
        }

        return errors;
    }

    /**
     * Attempts to validate the provided {@code JobParameters}. If the JobParameters is valid, null will be
     * returned. If the JobParameters has errors, a list of those errors will be returned.
     */
    @Nullable
    @Override
    @CallSuper
    public List<String> validate(JobParameters job) {
        List<String> errors = null;

        errors = mergeErrorLists(errors, validate(job.getTrigger()));
        errors = mergeErrorLists(errors, validate(job.getRetryStrategy()));

        if (job.isRecurring() && job.getTrigger() == Trigger.NOW) {
            errors = addError(errors, "ImmediateTriggers can't be used with recurring jobs");
        }

        errors = mergeErrorLists(errors, validateForTransport(job.getExtras()));
        if (job.getLifetime() > Lifetime.UNTIL_NEXT_BOOT) {
            //noinspection ConstantConditions
            errors = mergeErrorLists(errors, validateForPersistence(job.getExtras()));
        }

        errors = mergeErrorLists(errors, validateTag(job.getTag()));
        errors = mergeErrorLists(errors, validateService(job.getService()));

        return errors;
    }

    /**
     * Attempts to validate the provided Trigger. If valid, null is returned. Otherwise a list of
     * errors will be returned.
     * <p>
     * Note that a Trigger that passes validation here is not necessarily valid in all permutations
     * of a JobParameters. For example, an Immediate is never valid for a recurring job.
     * @param trigger
     */
    @Nullable
    @Override
    @CallSuper
    public List<String> validate(JobTrigger trigger) {
        if (trigger != Trigger.NOW
                && !(trigger instanceof JobTrigger.ExecutionWindowTrigger)
                && !(trigger instanceof JobTrigger.ContentUriTrigger)) {
            return getMutableSingletonList("Unknown trigger provided");
        }

        return null;
    }

    /**
     * Attempts to validate the provided RetryStrategy. If valid, null is returned. Otherwise a list
     * of errors will be returned.
     */
    @Nullable
    @Override
    @CallSuper
    public List<String> validate(RetryStrategy retryStrategy) {
        List<String> errors = null;

        int policy = retryStrategy.getPolicy();
        int initial = retryStrategy.getInitialBackoff();
        int maximum = retryStrategy.getMaximumBackoff();

        errors = addErrorsIf(policy != RETRY_POLICY_EXPONENTIAL && policy != RETRY_POLICY_LINEAR,
            errors, "Unknown retry policy provided");
        errors = addErrorsIf(maximum < initial,
            errors, "Maximum backoff must be greater than or equal to initial backoff");
        errors = addErrorsIf(300 > maximum,
            errors, "Maximum backoff must be greater than 300s (5 minutes)");
        errors = addErrorsIf(initial < 30,
            errors, "Initial backoff must be at least 30s");

        return errors;
    }

    @Nullable
    private List<String> validateForPersistence(Bundle extras) {
        List<String> errors = null;

        if (extras != null) {
            // check the types to make sure they're persistable
            for (String k : extras.keySet()) {
                errors = addError(errors, validateExtrasType(extras, k));
            }
        }

        return errors;
    }

    @Nullable
    private List<String> validateForTransport(Bundle extras) {
        if (extras == null) {
            return null;
        }

        int bundleSizeInBytes = measureBundleSize(extras);
        if (bundleSizeInBytes > MAX_EXTRAS_SIZE_BYTES) {
            return getMutableSingletonList(String.format(Locale.US,
                "Extras too large: %d bytes is > the max (%d bytes)",
                bundleSizeInBytes, MAX_EXTRAS_SIZE_BYTES));
        }

        return null;
    }

    @Nullable
    private String validateExtrasType(Bundle extras, String key) {
        Object o = extras.get(key);

        if (o == null
            || o instanceof Integer
            || o instanceof Long
            || o instanceof Double
            || o instanceof String
            || o instanceof Boolean) {
            return null;
        }

        return String.format(Locale.US,
            "Received value of type '%s' for key '%s', but only the"
                + " following extra parameter types are supported:"
                + " Integer, Long, Double, String, and Boolean",
            o == null ? null : o.getClass(), key);
    }

    private List<String> validateService(String service) {
        if (service == null || service.isEmpty()) {
            return getMutableSingletonList("Service can't be empty");
        }

        if (context == null) {
            return getMutableSingletonList("Context is null, can't query PackageManager");
        }

        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return getMutableSingletonList("PackageManager is null, can't validate service");
        }

        final String msg = "Couldn't find a registered service with the name " + service
            + ". Is it declared in the manifest with the right intent-filter?";

        Intent executeIntent = new Intent(JobService.ACTION_EXECUTE);
        executeIntent.setClassName(context, service);
        List<ResolveInfo> intentServices = pm.queryIntentServices(executeIntent, 0);
        if (intentServices == null || intentServices.isEmpty()) {
            return getMutableSingletonList(msg);
        }

        for (ResolveInfo info : intentServices) {
            if (info.serviceInfo != null && info.serviceInfo.enabled) {
                // found a match!
                return null;
            }
        }

        return getMutableSingletonList(msg);
    }

    private List<String> validateTag(String tag) {
        if (tag == null) {
            return getMutableSingletonList("Tag can't be null");
        }

        if (tag.length() > MAX_TAG_LENGTH) {
            return getMutableSingletonList("Tag must be shorter than " + MAX_TAG_LENGTH);
        }

        return null;
    }

    @NonNull
    private static List<String> getMutableSingletonList(String msg) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(msg);
        return strings;
    }
}
