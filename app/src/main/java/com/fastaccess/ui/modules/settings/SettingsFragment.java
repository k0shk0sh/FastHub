package com.fastaccess.ui.modules.settings;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.tasks.notification.NotificationSchedulerJobTask;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import es.dmoral.toasty.Toasty;

/**
 * Created by Kosh on 02 Mar 2017, 7:51 PM
 */

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fasthub_settings);
        findPreference("notificationTime").setOnPreferenceChangeListener(this);
        findPreference("recylerViewAnimation").setOnPreferenceChangeListener(this);
        findPreference("rect_avatar").setOnPreferenceChangeListener(this);
        findPreference("appTheme").setOnPreferenceChangeListener(this);
        findPreference("currentVersion").setSummary(SpannableBuilder.builder()
                .append(getString(R.string.current_version))
                .append("(")
                .bold(BuildConfig.VERSION_NAME)
                .append(")"));
        findPreference("aboutLibs").setOnPreferenceClickListener(preference -> {
            new LibsBuilder()
                    .withActivityStyle(AppHelper.isNightMode(getResources()) ? Libs.ActivityStyle.DARK :
                                       Libs.ActivityStyle.LIGHT)
                    .withFields(R.string.class.getFields())
                    .withAutoDetect(true)
                    .withAboutIconShown(true)
                    .withAboutVersionShown(true)
                    .start(getActivity());
            return true;
        });
    }

    @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equalsIgnoreCase("notificationTime")) {
            NotificationSchedulerJobTask.scheduleJob(getActivity().getApplicationContext(),
                    PrefGetter.getNotificationTaskDuration(getActivity().getApplicationContext()), true);
            return true;
        } else if (preference.getKey().equalsIgnoreCase("recylerViewAnimation")) {
            restartActivity();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("rect_avatar")) {
            restartActivity();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("appTheme")) {
            restartActivity();
            Toasty.warning(getContext(), getString(R.string.change_theme_warning), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void restartActivity() {
        if (getActivity() != null) {
            getActivity().recreate();
        }
    }
}
