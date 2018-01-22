package com.fastaccess.ui.modules.settings.category;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.widget.Toast;

import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.data.dao.SettingsModel;
import com.fastaccess.data.dao.model.SearchHistory;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.provider.tasks.notification.NotificationSchedulerJobTask;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.settings.sound.NotificationSoundBottomSheet;
import com.fastaccess.ui.modules.settings.sound.NotificationSoundMvp;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class SettingsCategoryFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener, NotificationSoundMvp.NotificationSoundListener {

    public static final String TAG = SettingsCategoryFragment.class.getSimpleName();

    public interface SettingsCallback {
        @SettingsModel.SettingsType int getSettingsType();
    }

    private static int PERMISSION_REQUEST_CODE = 128;
    private static int RESTORE_REQUEST_CODE = 256;
    private static int SOUND_REQUEST_CODE = 257;

    private BaseMvp.FAView callback;
    private String appColor;
    private String appLanguage;

    private Preference notificationTime;
    private Preference notificationRead;
    private Preference notificationSound;
    private Preference notificationSoundPath;
    private SettingsCallback settingsCallback;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        this.callback = (BaseMvp.FAView) context;
        this.settingsCallback = (SettingsCallback) context;
        appColor = PrefHelper.getString("appColor");
        appLanguage = PrefHelper.getString("app_language");
    }

    @Override public void onDetach() {
        callback = null;
        settingsCallback = null;
        super.onDetach();
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        switch (settingsCallback.getSettingsType()) {
            case SettingsModel.BACKUP:
                addBackup();
                break;
            case SettingsModel.BEHAVIOR:
                addBehaviour();
                break;
            case SettingsModel.CUSTOMIZATION:
                addCustomization();
                break;
            case SettingsModel.NOTIFICATION:
                addNotifications();
                break;
            default:
                Toast.makeText(App.getInstance(), "You reached the impossible :'(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equalsIgnoreCase("notificationEnabled")) {
            if ((boolean) newValue) {
                getPreferenceScreen().addPreference(notificationTime);
                getPreferenceScreen().addPreference(notificationRead);
                getPreferenceScreen().addPreference(notificationSound);
                getPreferenceScreen().addPreference(notificationSoundPath);
                NotificationSchedulerJobTask.scheduleJob(App.getInstance(),
                        PrefGetter.getNotificationTaskDuration(), true);
            } else {
                getPreferenceScreen().removePreference(notificationTime);
                getPreferenceScreen().removePreference(notificationRead);
                getPreferenceScreen().removePreference(notificationSound);
                getPreferenceScreen().removePreference(notificationSoundPath);
                NotificationSchedulerJobTask.scheduleJob(App.getInstance(), -1, true);
            }
            return true;
        } else if (preference.getKey().equalsIgnoreCase("notificationTime")) {
            NotificationSchedulerJobTask.scheduleJob(App.getInstance(),
                    PrefGetter.notificationDurationMillis((String) newValue), true);
            return true;
        } else if (preference.getKey().equalsIgnoreCase("recylerViewAnimation")) {
            callback.onThemeChanged();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("rect_avatar")) {
            callback.onThemeChanged();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("appColor")) {
            if (newValue.toString().equalsIgnoreCase(appColor))
                return true;
            Toasty.warning(App.getInstance(), getString(R.string.change_theme_warning), Toast.LENGTH_LONG).show();
            callback.onThemeChanged();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("app_language")) {
            if (newValue.toString().equalsIgnoreCase(appLanguage))
                return true;
            callback.onThemeChanged();
            return true;
        }
        return false;
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Map<String, ?> settings = PrefHelper.getAll();
                    settings.remove("token");
                    String json = new Gson().toJson(settings);
                    String path = Environment.getExternalStorageDirectory() + File.separator + "FastHub";
                    File folder = new File(path);
                    folder.mkdirs();
                    File backup = new File(folder, "backup.json");
                    try {
                        backup.createNewFile();
                        FileOutputStream outputStream = new FileOutputStream(backup);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(outputStream);
                        myOutWriter.append(json);

                        myOutWriter.close();

                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        Log.e(getTag(), "Couldn't backup: " + e.toString());
                    }
                    PrefHelper.set("backed_up", new SimpleDateFormat("MM/dd", Locale.ENGLISH).format(new Date()));
                    findPreference("backup").setSummary(getString(R.string.backup_summary, getString(R.string.now)));
                    Toasty.success(App.getInstance(), getString(R.string.backed_up)).show();
                } else {
                    Toasty.error(App.getInstance(), getString(R.string.permission_failed)).show();
                }
            } else if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser();
                } else {
                    Toasty.error(App.getInstance(), getString(R.string.permission_failed)).show();
                }
            }
        }

    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RESTORE_REQUEST_CODE) {
                restoreData(data);
            } else if (requestCode == SOUND_REQUEST_CODE) {
                Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (notificationSoundPath != null && notificationSoundPath.isVisible()) {
                    notificationSoundPath.setDefaultValue(ringtone.toString());
                }
            }
        }
    }

    @Override public void onSoundSelected(Uri uri) {
        PrefGetter.setNotificationSound(uri);
        if (notificationSoundPath != null && notificationSoundPath.isVisible())
            notificationSoundPath.setSummary(FileHelper.getRingtoneName(getContext(), uri));
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/json");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_backup)), RESTORE_REQUEST_CODE);
    }

    private void addBackup() {
        addPreferencesFromResource(R.xml.backup_settings);
        findPreference("backup").setOnPreferenceClickListener((Preference preference) -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Map<String, ?> preferences = PrefHelper.getAll();
                preferences.remove("token");
                String json = new Gson().toJson(preferences);
                String path = FileHelper.PATH;
                File folder = new File(path);
                folder.mkdirs();
                File backup = new File(folder, "backup.json");
                try {
                    backup.createNewFile();
                    try (FileOutputStream outputStream = new FileOutputStream(backup)) {
                        try (OutputStreamWriter myOutWriter = new OutputStreamWriter(outputStream)) {
                            myOutWriter.append(json);
                        }
                    }
                } catch (IOException e) {
                    Log.e(getTag(), "Couldn't backup: " + e.toString());
                }
                PrefHelper.set("backed_up", new SimpleDateFormat("MM/dd", Locale.ENGLISH).format(new Date()));
                Toasty.success(App.getInstance(), getString(R.string.backed_up)).show();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
            return true;
        });
        if (PrefHelper.getString("backed_up") != null) {
            findPreference("backup").setSummary(SpannableBuilder.builder()
                    .append(getString(R.string.backup_summary, PrefHelper.getString("backed_up")))
                    .append("\n")
                    .append(FileHelper.PATH));
        } else {
            findPreference("backup").setSummary("");
        }
        findPreference("restore").setOnPreferenceClickListener(preference -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showFileChooser();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
            return true;
        });
    }

    private void addCustomization() {
        addPreferencesFromResource(R.xml.customization_settings);
        findPreference("enable_ads").setVisible(false);
        findPreference("recylerViewAnimation").setOnPreferenceChangeListener(this);
        findPreference("rect_avatar").setOnPreferenceChangeListener(this);
        findPreference("appColor").setOnPreferenceChangeListener(this);
    }

    private void addBehaviour() {
        addPreferencesFromResource(R.xml.behaviour_settings);
        findPreference("clear_search").setOnPreferenceClickListener(preference -> {
            callback.showMessage(R.string.success, R.string.deleted);
            SearchHistory.deleteAll();
            return true;
        });
    }

    private void addNotifications() {
        addPreferencesFromResource(R.xml.notification_settings);
        notificationTime = findPreference("notificationTime");
        notificationRead = findPreference("markNotificationAsRead");
        notificationSound = findPreference("notificationSound");
        notificationTime.setOnPreferenceChangeListener(this);
        findPreference("notificationEnabled").setOnPreferenceChangeListener(this);
        notificationSoundPath = findPreference("notification_sound_path");
        notificationSoundPath.setSummary(FileHelper.getRingtoneName(getContext(), PrefGetter.getNotificationSound()));
        notificationSoundPath.setOnPreferenceClickListener(preference -> {
            NotificationSoundBottomSheet.Companion.newInstance(FileHelper.getRingtoneName(getContext(), PrefGetter.getNotificationSound()))
                    .show(getChildFragmentManager(), "NotificationSoundBottomSheet");
            return true;
        });
        if (!PrefHelper.getBoolean("notificationEnabled")) {
            getPreferenceScreen().removePreference(notificationTime);
            getPreferenceScreen().removePreference(notificationRead);
            getPreferenceScreen().removePreference(notificationSound);
            getPreferenceScreen().removePreference(notificationSoundPath);
        }
    }

    private void restoreData(Intent data) {
        StringBuilder json = new StringBuilder();
        try {
            try (InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData())) {
                if (inputStream != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            json.append(line);
                        }
                    }
                }
            }
        } catch (IOException e) {
            Toasty.error(App.getInstance(), getString(R.string.error)).show();
        }
        if (!InputHelper.isEmpty(json)) {
            try {
                Gson gson = new Gson();
                Type typeOfHashMap = new TypeToken<Map<String, ?>>() {}.getType();
                Map<String, ?> savedPref = gson.fromJson(json.toString(), typeOfHashMap);
                if (savedPref != null && !savedPref.isEmpty()) {
                    for (Map.Entry<String, ?> stringEntry : savedPref.entrySet()) {
                        PrefHelper.set(stringEntry.getKey(), stringEntry.getKey());
                    }
                }
                callback.onThemeChanged();
            } catch (Exception ignored) {
                Toasty.error(App.getInstance(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
