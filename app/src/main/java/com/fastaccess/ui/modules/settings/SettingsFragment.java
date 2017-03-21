package com.fastaccess.ui.modules.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.fastaccess.R;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.tasks.notification.NotificationSchedulerJobTask;

/**
 * Created by Kosh on 02 Mar 2017, 7:51 PM
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fasthub_settings);
        findPreference("notificationTime").setOnPreferenceChangeListener((preference, newValue) -> {
            NotificationSchedulerJobTask.scheduleJob(getActivity().getApplicationContext(), PrefGetter.getNotificationTaskDuration(getActivity()
                    .getApplicationContext()), true);
            return true;
        });
        findPreference("recylerViewAnimation").setOnPreferenceChangeListener((preference, newValue) -> {
            restartActivity();
            return true;
        });
        findPreference("rect_avatar").setOnPreferenceChangeListener((preference, newValue) -> {
            restartActivity();
            return true;
        });
        findPreference("appTheme").setOnPreferenceChangeListener((preference, newValue) -> {
            restartActivity();
            return true;
        });
    }

    private void restartActivity() {if (getActivity() != null) getActivity().recreate();}

}
