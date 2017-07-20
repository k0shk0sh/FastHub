package com.fastaccess.provider.tasks.slack;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.data.dao.SlackInvitePostModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;

import es.dmoral.toasty.Toasty;

/**
 * Created by Kosh on 01 May 2017, 1:09 AM
 */

public class SlackInvitationService extends IntentService {

    public SlackInvitationService() {
        super(SlackInvitationService.class.getName());
    }

    @Override protected void onHandleIntent(@Nullable Intent intent) {
        Login login = Login.getUser();
        if (login != null) {
            SlackInvitePostModel body = new SlackInvitePostModel();
            body.setEmail(login.getEmail());
            body.setFirst_name(login.getName());
            body.setLast_name(login.getLogin());
            RxHelper.getObservable(RestProvider.getSlackService()
                    .invite(body))
                    .subscribe(response -> {
                        if (response != null) {
                            if (response.isOk()) {
                                Toasty.success(App.getInstance(), getString(R.string.successfully_invited)).show();
                            } else {
                                Toasty.info(App.getInstance(), response.getError().replaceAll("_", " ")).show();
                            }
                        }
                    }, Throwable::printStackTrace);
        }
    }
}
