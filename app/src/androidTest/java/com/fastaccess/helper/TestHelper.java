package com.fastaccess.helper;

import android.support.design.widget.TextInputLayout;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by Kosh on 05 May 2017, 9:23 PM
 */

public class TestHelper {

    public static Matcher<View> textInputLayoutHasHint(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {
            @Override public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }
                CharSequence error = ((TextInputLayout) view).getHint();
                return error != null && expectedErrorText.equals(error.toString());
            }

            @Override public void describeTo(Description description) {}
        };
    }

    public static Matcher<View> textInputLayoutHasError(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {
            @Override public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }
                CharSequence error = ((TextInputLayout) view).getError();
                return error != null && expectedErrorText.equals(error.toString());
            }

            @Override public void describeTo(Description description) {}
        };
    }
}
