package com.fastaccess.ui.modules.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.provider.tasks.notification.NotificationSchedulerJobTask;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.changelog.ChangelogBottomSheetDialog;
import com.fastaccess.ui.widgets.SpannableBuilder;

import es.dmoral.toasty.Toasty;

/**
 * Created by Kosh on 02 Mar 2017, 7:51 PM
 */

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private BaseMvp.FAView callback;
    private String appTheme;
    private String appColor;
    private String app_lauguage;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseMvp.FAView) {
            callback = (BaseMvp.FAView) context;
        }

        appTheme = PrefHelper.getString("appTheme");
        appColor = PrefHelper.getString("appColor");
        app_lauguage = PrefHelper.getString("app_language");
    }

    @Override public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fasthub_settings);
        findPreference("notificationTime").setOnPreferenceChangeListener(this);
        findPreference("recylerViewAnimation").setOnPreferenceChangeListener(this);
        findPreference("rect_avatar").setOnPreferenceChangeListener(this);
        findPreference("appTheme").setOnPreferenceChangeListener(this);
        findPreference("appColor").setOnPreferenceChangeListener(this);
        findPreference("app_language").setOnPreferenceChangeListener(this);
        findPreference("showChangelog").setOnPreferenceClickListener(preference -> {
            new ChangelogBottomSheetDialog().show(getChildFragmentManager(), "ChangelogBottomSheetDialog");
            return true;
        });
        findPreference("joinSlack").setOnPreferenceClickListener(preference -> {
            ActivityHelper.startCustomTab(getActivity(), "http://rebrand.ly/fasthub");
            return true;
        });
        findPreference("currentVersion").setSummary(SpannableBuilder.builder()
                .append(getString(R.string.current_version))
                .append("(")
                .bold(BuildConfig.VERSION_NAME)
                .append(")"));
        if (BuildConfig.FDROID) {
            findPreference("enable_ads").setVisible(false);
        }
    }

    @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equalsIgnoreCase("notificationTime")) {
            NotificationSchedulerJobTask.scheduleJob(getActivity().getApplicationContext(),
                    PrefGetter.notificationDurationMillis(getActivity().getApplicationContext(), (String) newValue), true);
            return true;
        } else if (preference.getKey().equalsIgnoreCase("recylerViewAnimation")) {
            restartActivity();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("rect_avatar")) {
            restartActivity();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("appTheme")) {
            if(newValue.toString().equalsIgnoreCase(appTheme))
                return true;
            Toasty.warning(getContext(), getString(R.string.change_theme_warning), Toast.LENGTH_LONG).show();
            restartActivity();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("appColor")) {
            if(newValue.toString().equalsIgnoreCase(appColor))
                return true;
            Toasty.warning(getContext(), getString(R.string.change_theme_warning), Toast.LENGTH_LONG).show();
            restartActivity();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("app_language")) {
            if(newValue.toString().equalsIgnoreCase(app_lauguage))
                return true;
            restartActivity();
            return true;
        }
        return false;
    }

    private void restartActivity() {
        if (callback != null) {
            callback.onThemeChanged();
        }
    }
}
