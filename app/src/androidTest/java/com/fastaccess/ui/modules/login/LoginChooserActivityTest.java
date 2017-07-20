package com.fastaccess.ui.modules.login;


import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.fastaccess.R;
import com.fastaccess.ui.modules.login.chooser.LoginChooserActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.fastaccess.helper.TestHelper.textInputLayoutHasHint;

@RunWith(AndroidJUnit4.class) @LargeTest
public class LoginChooserActivityTest {

    @Rule public IntentsTestRule<LoginChooserActivity> intentTestRule = new IntentsTestRule<>(LoginChooserActivity.class);

    @Test public void basicAuthButtonTest() {
        onView(withId(R.id.basicAuth)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
        onView(withId(R.id.password)).check(matches(textInputLayoutHasHint(intentTestRule.getActivity().getString(R.string.password))));
    }

    @Test public void accessTokenButtonTest() {
        onView(withId(R.id.accessToken)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
        onView(withId(R.id.password)).check(matches(textInputLayoutHasHint(intentTestRule.getActivity().getString(R.string.access_token))));
    }
}