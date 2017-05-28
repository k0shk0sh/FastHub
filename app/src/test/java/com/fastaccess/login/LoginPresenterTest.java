package com.fastaccess.login;

import com.fastaccess.ui.modules.login.LoginMvp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

/**
 * Created by Kosh on 02 May 2017, 7:52 PM
 */

@RunWith(JUnit4.class)
public class LoginPresenterTest {

    @Mock private LoginMvp.View view;

    @Before public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//        final TiPresenterInstructor<LoginMvp.View> instructor = new TiPresenterInstructor<>(new LoginPresenter());
//        instructor.attachView(view);
    }

    @Test public void onTokenResponse() throws Exception {}

    @Test public void onHandleAuthIntent() throws Exception {

    }

    @Test public void onUserResponse() throws Exception {

    }

    @Test public void login() throws Exception {

    }

}
