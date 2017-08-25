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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.NONE, sdk = 23)
public class ValidationEnforcerTest {
    private static final List<String> ERROR_LIST = Collections.singletonList("error: foo");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private JobValidator mValidator;

    @Mock
    private JobParameters mMockJobParameters;

    @Mock
    private JobTrigger mMockTrigger;

    private ValidationEnforcer mEnforcer;
    private RetryStrategy mRetryStrategy = RetryStrategy.DEFAULT_EXPONENTIAL;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        mEnforcer = new ValidationEnforcer(mValidator);
    }

    @Test
    public void testValidate_retryStrategy() throws Exception {
        mEnforcer.validate(mRetryStrategy);
        verify(mValidator).validate(mRetryStrategy);
    }

    @Test
    public void testValidate_jobSpec() throws Exception {
        mEnforcer.validate(mMockJobParameters);
        verify(mValidator).validate(mMockJobParameters);
    }

    @Test
    public void testValidate_trigger() throws Exception {
        mEnforcer.validate(mMockTrigger);
        verify(mValidator).validate(mMockTrigger);
    }

    @Test
    public void testIsValid_retryStrategy_invalid() throws Exception {
        when(mValidator.validate(mRetryStrategy))
            .thenReturn(Collections.singletonList("error: foo"));

        assertFalse("isValid", mEnforcer.isValid(mRetryStrategy));
    }

    @Test
    public void testIsValid_retryStrategy_valid() throws Exception {
        when(mValidator.validate(mRetryStrategy)).thenReturn(null);

        assertTrue("isValid", mEnforcer.isValid(mRetryStrategy));

    }

    @Test
    public void testIsValid_trigger_invalid() throws Exception {
        when(mValidator.validate(mMockTrigger))
            .thenReturn(Collections.singletonList("error: foo"));

        assertFalse("isValid", mEnforcer.isValid(mMockTrigger));
    }

    @Test
    public void testIsValid_trigger_valid() throws Exception {
        when(mValidator.validate(mMockTrigger)).thenReturn(null);

        assertTrue("isValid", mEnforcer.isValid(mMockTrigger));
    }

    @Test
    public void testIsValid_jobSpec_invalid() throws Exception {
        when(mValidator.validate(mMockJobParameters)).thenReturn(ERROR_LIST);

        assertFalse("isValid", mEnforcer.isValid(mMockJobParameters));
    }

    @Test
    public void testIsValid_jobSpec_valid() throws Exception {
        when(mValidator.validate(mMockJobParameters)).thenReturn(null);

        assertTrue("isValid", mEnforcer.isValid(mMockJobParameters));
    }

    @Test
    public void testEnsureValid_retryStrategy_valid() throws Exception {
        when(mValidator.validate(mRetryStrategy)).thenReturn(null);

        mEnforcer.ensureValid(mRetryStrategy);
    }

    @Test
    public void testEnsureValid_trigger_valid() throws Exception {
        when(mValidator.validate(mMockTrigger)).thenReturn(null);

        mEnforcer.ensureValid(mMockTrigger);
    }

    @Test
    public void testEnsureValid_jobSpec_valid() throws Exception {
        when(mValidator.validate(mMockJobParameters)).thenReturn(null);

        mEnforcer.ensureValid(mMockJobParameters);
    }

    @Test
    public void testEnsureValid_retryStrategy_invalid() throws Exception {
        expectedException.expect(ValidationEnforcer.ValidationException.class);

        when(mValidator.validate(mRetryStrategy)).thenReturn(ERROR_LIST);
        mEnforcer.ensureValid(mRetryStrategy);
    }

    @Test
    public void testEnsureValid_trigger_invalid() throws Exception {
        expectedException.expect(ValidationEnforcer.ValidationException.class);

        when(mValidator.validate(mMockTrigger)).thenReturn(ERROR_LIST);
        mEnforcer.ensureValid(mMockTrigger);
    }

    @Test
    public void testEnsureValid_jobSpec_invalid() throws Exception {
        expectedException.expect(ValidationEnforcer.ValidationException.class);

        when(mValidator.validate(mMockJobParameters)).thenReturn(ERROR_LIST);
        mEnforcer.ensureValid(mMockJobParameters);
    }

    @Test
    public void testValidationMessages() throws Exception {
        when(mValidator.validate(mMockJobParameters)).thenReturn(ERROR_LIST);

        try {
            mEnforcer.ensureValid(mMockJobParameters);

            fail("Expected ensureValid to fail");
        } catch (ValidationEnforcer.ValidationException ve) {
            assertEquals("Expected ValidationException to have 1 error message",
                1,
                ve.getErrors().size());
        }
    }
}
