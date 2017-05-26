package com.fastaccess.helper;

import android.support.annotation.IntRange;
import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

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

    public static Matcher<View> bottomNavSelection(@IntRange(from = 0, to = 3) final int position) {
        return new TypeSafeMatcher<View>() {
            @Override public boolean matchesSafely(View view) {
                return view instanceof BottomNavigation && position == ((BottomNavigation) view).getSelectedIndex();
            }

            @Override public void describeTo(Description description) {}
        };
    }

    public static ViewAction bottomNavAction(@IntRange(from = 0, to = 3) final int index) {
        return new ViewAction() {

            @Override public Matcher<View> getConstraints() {
                return isAssignableFrom(BottomNavigation.class);
            }

            @Override public String getDescription() {
                return "BottomNavigation";
            }

            @Override public void perform(UiController uiController, View view) {
                ((BottomNavigation) view).setSelectedIndex(index, false);
            }
        };
    }
}
