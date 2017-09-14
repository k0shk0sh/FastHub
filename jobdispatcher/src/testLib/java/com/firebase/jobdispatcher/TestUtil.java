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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.os.Binder;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.ContactsContract;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import com.firebase.jobdispatcher.Job.Builder;
import com.firebase.jobdispatcher.JobTrigger.ContentUriTrigger;
import com.firebase.jobdispatcher.ObservedUri.Flags;
import com.google.android.gms.gcm.PendingCallback;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Provides common utilities helpful for testing.
 */
public class TestUtil {

    private static final String TAG = "TAG";
    private static final String[] TAG_COMBINATIONS = {"tag", "foobar", "fooooooo", "bz", "100"};

    private static final int[] LIFETIME_COMBINATIONS = {
        Lifetime.UNTIL_NEXT_BOOT,
        Lifetime.FOREVER};

    private static final JobTrigger[] TRIGGER_COMBINATIONS = {
        Trigger.executionWindow(0, 30),
        Trigger.executionWindow(300, 600),
        Trigger.executionWindow(86400, 86400 * 2),
        Trigger.NOW,
        Trigger.contentUriTrigger(
            Arrays.asList(new ObservedUri(ContactsContract.AUTHORITY_URI, 0))),
        Trigger.contentUriTrigger(Arrays.asList(new ObservedUri(ContactsContract.AUTHORITY_URI, 0),
            new ObservedUri(ContactsContract.AUTHORITY_URI, Flags.FLAG_NOTIFY_FOR_DESCENDANTS)))
    };

    @SuppressWarnings("unchecked")
    private static final List<Class<TestJobService>> SERVICE_COMBINATIONS =
        Arrays.asList(TestJobService.class);

    private static final RetryStrategy[] RETRY_STRATEGY_COMBINATIONS = {
        RetryStrategy.DEFAULT_LINEAR,
        new RetryStrategy(RetryStrategy.RETRY_POLICY_LINEAR, 60, 300),
        RetryStrategy.DEFAULT_EXPONENTIAL,
        new RetryStrategy(RetryStrategy.RETRY_POLICY_EXPONENTIAL, 300, 600),
    };

    public static void assertHasSinglePrivateConstructor(Class<?> cls) throws Exception {
        Constructor<?>[] constructors = cls.getDeclaredConstructors();
        assertEquals("expected number of constructors to be == 1", 1, constructors.length);

        Constructor<?> constructor = constructors[0];
        assertFalse("expected constructor to be inaccessible", constructor.isAccessible());

        constructor.setAccessible(true);
        constructor.newInstance();
    }

    static List<List<Integer>> getAllConstraintCombinations() {
        List<List<Integer>> combos = new LinkedList<>();

        combos.add(Collections.<Integer>emptyList());
        for (Integer cur : Constraint.ALL_CONSTRAINTS) {
            for (int l = combos.size() - 1; l >= 0; l--) {
                List<Integer> oldCombo = combos.get(l);
                List<Integer> newCombo = Arrays.asList(new Integer[oldCombo.size() + 1]);

                Collections.copy(newCombo, oldCombo);
                newCombo.set(oldCombo.size(), cur);
                combos.add(newCombo);
            }
            combos.add(Collections.singletonList(cur));
        }

        return combos;
    }

    static int[] toIntArray(List<Integer> list) {
        int[] input = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            input[i] = list.get(i);
        }
        return input;
    }

    static List<Job> getJobCombinations(Builder builder) {
        return getCombination(new JobBuilder(builder));
    }

    static List<JobInvocation> getJobInvocationCombinations() {
        return getCombination(new JobInvocationBuilder());
    }

    private static <T extends JobParameters> List<T> getCombination(
            JobParameterBuilder<T> buildJobParam) {

        List<T> result = new ArrayList<>();
        for (String tag : TAG_COMBINATIONS) {
            for (List<Integer> constraintList : getAllConstraintCombinations()) {
                for (boolean recurring : new boolean[]{true, false}) {
                    for (boolean replaceCurrent : new boolean[]{true, false}) {
                        for (int lifetime : LIFETIME_COMBINATIONS) {
                            for (JobTrigger trigger : TRIGGER_COMBINATIONS) {
                                for (Class<TestJobService> service : SERVICE_COMBINATIONS) {
                                    for (Bundle extras : getBundleCombinations()) {
                                        for (RetryStrategy rs : RETRY_STRATEGY_COMBINATIONS) {
                                            result.add(buildJobParam.build(
                                                tag,
                                                replaceCurrent,
                                                constraintList,
                                                recurring,
                                                lifetime,
                                                trigger,
                                                service,
                                                extras,
                                                rs));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private static Bundle[] getBundleCombinations() {
        List<Bundle> bundles = new LinkedList<>();
        bundles.add(new Bundle());

        Bundle b = new Bundle();
        b.putString("foo", "bar");
        b.putInt("bar", 1);
        b.putLong("baz", 3L);
        bundles.add(b);

        return bundles.toArray(new Bundle[bundles.size()]);
    }

    static void assertJobsEqual(JobParameters input, JobParameters output) {
        assertNotNull("input", input);
        assertNotNull("output", output);

        assertEquals("isRecurring()", input.isRecurring(), output.isRecurring());
        assertEquals("shouldReplaceCurrent()",
            input.shouldReplaceCurrent(),
            output.shouldReplaceCurrent());
        assertEquals("getLifetime()", input.getLifetime(), output.getLifetime());
        assertEquals("getTag()", input.getTag(), output.getTag());
        assertEquals("getService()", input.getService(), output.getService());
        assertEquals("getConstraints()",
            Constraint.compact(input.getConstraints()),
            Constraint.compact(output.getConstraints()));

        assertTriggersEqual(input.getTrigger(), output.getTrigger());
        assertBundlesEqual(input.getExtras(), output.getExtras());
        assertRetryStrategiesEqual(input.getRetryStrategy(), output.getRetryStrategy());
    }

    static void assertRetryStrategiesEqual(RetryStrategy in, RetryStrategy out) {
        String prefix = "getRetryStrategy().";

        assertEquals(prefix + "getPolicy()",
            in.getPolicy(), out.getPolicy());
        assertEquals(prefix + "getInitialBackoff()",
            in.getInitialBackoff(), out.getInitialBackoff());
        assertEquals(prefix + "getMaximumBackoff()",
            in.getMaximumBackoff(), out.getMaximumBackoff());
    }

    static void assertBundlesEqual(Bundle inExtras, Bundle outExtras) {
        if (inExtras == null || outExtras == null) {
            assertNull(inExtras);
            assertNull(outExtras);
            return;
        }

        assertEquals("getExtras().size()", inExtras.size(), outExtras.size());
        final Set<String> inKeys = inExtras.keySet();
        for (String key : inKeys) {
            assertTrue("getExtras().containsKey(\"" + key + "\")", outExtras.containsKey(key));
            assertEquals("getExtras().get(\"" + key + "\")", inExtras.get(key), outExtras.get(key));
        }
    }

    static void assertTriggersEqual(JobTrigger inTrigger, JobTrigger outTrigger) {
        assertEquals("", inTrigger.getClass(), outTrigger.getClass());

        if (inTrigger instanceof JobTrigger.ExecutionWindowTrigger) {
            assertEquals("getTrigger().getWindowStart()",
                ((JobTrigger.ExecutionWindowTrigger) inTrigger).getWindowStart(),
                ((JobTrigger.ExecutionWindowTrigger) outTrigger).getWindowStart());
            assertEquals("getTrigger().getWindowEnd()",
                ((JobTrigger.ExecutionWindowTrigger) inTrigger).getWindowEnd(),
                ((JobTrigger.ExecutionWindowTrigger) outTrigger).getWindowEnd());
        } else if (inTrigger == Trigger.NOW) {
            assertEquals(inTrigger, outTrigger);
        } else if (inTrigger instanceof JobTrigger.ContentUriTrigger) {
            assertEquals("Collection of URIs",
                ((ContentUriTrigger) inTrigger).getUris(),
                ((ContentUriTrigger) outTrigger).getUris());
        } else {
            fail("Unknown Trigger class: " + inTrigger.getClass());
        }
    }

    @NonNull
    public static Builder getBuilderWithNoopValidator() {
        return new Builder(new ValidationEnforcer(new NoopJobValidator()));
    }

    @NonNull
    static Bundle encodeContentUriJob(ContentUriTrigger trigger, JobCoder coder) {
        Job job = getBuilderWithNoopValidator()
                .setTag(TAG)
                .setTrigger(trigger)
                .setService(TestJobService.class)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        return coder.encode(job, new Bundle());
    }

    @NonNull
    static Bundle encodeRecurringContentUriJob(ContentUriTrigger trigger, JobCoder coder) {
        Job job = getBuilderWithNoopValidator()
            .setTag(TAG)
            .setTrigger(trigger)
            .setService(TestJobService.class)
            .setReplaceCurrent(true)
            .setRecurring(true)
            .build();
        return coder.encode(job, new Bundle());
    }

    static ContentUriTrigger getContentUriTrigger() {
        ObservedUri contactUri = new ObservedUri(
            ContactsContract.AUTHORITY_URI, Flags.FLAG_NOTIFY_FOR_DESCENDANTS);
        ObservedUri imageUri = new ObservedUri(Media.EXTERNAL_CONTENT_URI, 0);
        return Trigger.contentUriTrigger(Arrays.asList(contactUri, imageUri));
    }

    public static class TransactionArguments {
        public final int code;
        public final Parcel data;
        public final int flags;

        public TransactionArguments(int code, Parcel data, int flags) {
            this.code = code;
            this.data = data;
            this.flags = flags;
        }
    }

    public static class InspectableBinder extends Binder {
        private final List<TransactionArguments> transactionArguments = new LinkedList<>();

        public InspectableBinder() {}

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            transactionArguments.add(new TransactionArguments(code, copyParcel(data), flags));
            return true;
        }

        public PendingCallback toPendingCallback() {
            Parcel container = Parcel.obtain();
            try {
                container.writeStrongBinder(this);
                container.setDataPosition(0);
                return new PendingCallback(container);
            } finally {
                container.recycle();
            }
        }

        private Parcel copyParcel(Parcel data) {
            Parcel clone = Parcel.obtain();
            clone.appendFrom(data, 0, data.dataSize());
            clone.setDataPosition(0);
            return clone;
        }

        public List<TransactionArguments> getArguments() {
            return Collections.unmodifiableList(transactionArguments);
        }
    }

    private static class JobInvocationBuilder implements
            JobParameterBuilder<JobInvocation> {

        @Override
        public JobInvocation build(String tag, boolean replaceCurrent, List<Integer> constraintList,
            boolean recurring, int lifetime, JobTrigger trigger, Class<TestJobService> service,
            Bundle extras, RetryStrategy rs) {
            //noinspection WrongConstant
            return new JobInvocation.Builder()
                    .setTag(tag)
                    .setReplaceCurrent(replaceCurrent)
                    .setRecurring(recurring)
                    .setConstraints(toIntArray(constraintList))
                    .setLifetime(lifetime)
                    .setTrigger(trigger)
                    .setService(service.getName())
                    .addExtras(extras)
                    .setRetryStrategy(rs)
                    .build();
        }
    }

    private static class JobBuilder implements JobParameterBuilder<Job> {

        private final Builder builder;

        public JobBuilder(Builder builder){
            this.builder = builder;
        }

        @Override
        public Job build(String tag, boolean replaceCurrent, List<Integer> constraintList,
                boolean recurring, int lifetime, JobTrigger trigger, Class<TestJobService> service,
                Bundle extras, RetryStrategy rs) {
            //noinspection WrongConstant
            return builder
                    .setTag(tag)
                    .setReplaceCurrent(replaceCurrent)
                    .setRecurring(recurring)
                    .setConstraints(toIntArray(constraintList))
                    .setLifetime(lifetime)
                    .setTrigger(trigger)
                    .setService(service)
                    .setExtras(extras)
                    .setRetryStrategy(rs)
                    .build();
        }
    }

    private interface JobParameterBuilder<T extends JobParameters> {

        T build(String tag, boolean replaceCurrent, List<Integer> constraintList, boolean recurring,
                int lifetime, JobTrigger trigger, Class<TestJobService> service, Bundle extras,
                RetryStrategy rs);
    }
}
