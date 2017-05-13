package com.fastaccess.ui.modules.settings.category;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.FrameLayout;
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

import butterknife.BindView;
import es.dmoral.toasty.Toasty;

public class SettingsCategoryFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @BindView(R.id.settingsContainer)
    FrameLayout settingsContainer;

    private BaseMvp.FAView callback;
    private String appTheme;
    private String appColor;
    private String app_lauguage;

    private Preference signatureVia;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        this.callback = (BaseMvp.FAView) context;
        appTheme = PrefHelper.getString("appTheme");
        appColor = PrefHelper.getString("appColor");
        app_lauguage = PrefHelper.getString("app_language");
    }

    @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        int settings = getActivity().getIntent().getExtras().getInt("settings", 0);
        switch (settings) {
            case 0:
                addPreferencesFromResource(R.xml.notification_settings);
                findPreference("notificationTime").setOnPreferenceChangeListener(this);
                break;
            case 1:
                addPreferencesFromResource(R.xml.behaviour_settings);
                findPreference("sent_via_enabled").setOnPreferenceChangeListener(this);
                signatureVia = findPreference("sent_via");
                if(PrefHelper.getBoolean("sent_via_enabled"))
                    getPreferenceScreen().removePreference(signatureVia);
                break;
            case 2:
                addPreferencesFromResource(R.xml.customization_settings);
                if (BuildConfig.FDROID) {
                    findPreference("enable_ads").setVisible(false);
                }
                findPreference("recylerViewAnimation").setOnPreferenceChangeListener(this);
                findPreference("rect_avatar").setOnPreferenceChangeListener(this);
                findPreference("appTheme").setOnPreferenceChangeListener(this);
                findPreference("appColor").setOnPreferenceChangeListener(this);
                break;
            case 3:
                addPreferencesFromResource(R.xml.about_settings);
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
                break;
            default:
                addPreferencesFromResource(R.xml.fasthub_settings);
                break;
        }
    }

    @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equalsIgnoreCase("notificationTime")) {
            NotificationSchedulerJobTask.scheduleJob(getActivity().getApplicationContext(),
                    PrefGetter.notificationDurationMillis(getActivity().getApplicationContext(), (String) newValue), true);
            return true;
        } else if (preference.getKey().equalsIgnoreCase("recylerViewAnimation")) {
            callback.onThemeChanged();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("rect_avatar")) {
            callback.onThemeChanged();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("appTheme")) {
            if (newValue.toString().equalsIgnoreCase(appTheme))
                return true;
            Toasty.warning(getContext(), getString(R.string.change_theme_warning), Toast.LENGTH_LONG).show();
            callback.onThemeChanged();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("appColor")) {
            if (newValue.toString().equalsIgnoreCase(appColor))
                return true;
            Toasty.warning(getContext(), getString(R.string.change_theme_warning), Toast.LENGTH_LONG).show();
            callback.onThemeChanged();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("app_language")) {
            if (newValue.toString().equalsIgnoreCase(app_lauguage))
                return true;
            callback.onThemeChanged();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("sent_via_enabled")) {
            if((boolean)newValue)
                getPreferenceScreen().removePreference(signatureVia);
            else
                getPreferenceScreen().addPreference(signatureVia);
            return true;
        }
        return false;
    }

}
