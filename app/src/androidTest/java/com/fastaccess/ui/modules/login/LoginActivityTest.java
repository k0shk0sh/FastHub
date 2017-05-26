package com.fastaccess.ui.modules.login;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.fastaccess.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.fastaccess.helper.TestHelper.textInputLayoutHasError;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class) @LargeTest
public class LoginActivityTest {

    @Rule public ActivityTestRule<LoginActivity> testRule = new ActivityTestRule<>(LoginActivity.class);

    @Test public void successLoginClickSuccessTest() {
        String username = "username";
        String password = "password";
        onView(withId(R.id.usernameEditText)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.progress)).check(matches(isDisplayed()));
    }

    @Test public void usernameErrorTest() {
        String password = "password";
        onView(withId(R.id.passwordEditText)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.username)).check(matches(textInputLayoutHasError(testRule.getActivity().getString(R.string.required_field))));
    }

    @Test public void passwordErrorTest() {
        String username = "username";
        onView(withId(R.id.usernameEditText)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.password)).check(matches(textInputLayoutHasError(testRule.getActivity().getString(R.string.required_field))));
    }

}