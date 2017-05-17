package com.fastaccess.ui.modules.settings.category;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Release;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.provider.tasks.notification.NotificationSchedulerJobTask;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.changelog.ChangelogBottomSheetDialog;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class SettingsCategoryFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @BindView(R.id.settingsContainer)
    FrameLayout settingsContainer;

    private static int PERMISSION_REQUEST_CODE = 128;
    private static int RESTORE_REQUEST_CODE = 256;

    private BaseMvp.FAView callback;
    private String appTheme;
    private String appColor;
    private String app_lauguage;

    private Preference signatureVia;
    private Preference notificationTime;
    private Preference notificationRead;
    private Preference notificationSound;

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
                notificationTime = findPreference("notificationTime");
                notificationRead = findPreference("markNotificationAsRead");
                notificationSound = findPreference("notificationSound");
                findPreference("notificationTime").setOnPreferenceChangeListener(this);
                findPreference("notificationEnabled").setOnPreferenceChangeListener(this);
                if(!PrefHelper.getBoolean("notificationEnabled")) {
                    getPreferenceScreen().removePreference(notificationTime);
                    getPreferenceScreen().removePreference(notificationRead);
                    getPreferenceScreen().removePreference(notificationSound);
                }
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
                findPreference("currentVersion").setOnPreferenceClickListener(preference -> {
                    Release.get("FastHub", "k0shk0sh").subscribe(releases -> {
                        if(releases.get(0).getTagName().equals(BuildConfig.VERSION_NAME))
                            Toasty.success(getContext(), getString(R.string.up_to_date)).show();
                        else
                            Toasty.warning(getContext(), getString(R.string.new_version)).show();
                    });


                    return true;
                });
                break;
            case 4:
                addPreferencesFromResource(R.xml.backup_settings);
                findPreference("backup").setOnPreferenceClickListener((Preference preference) -> {


                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Map<String, ?> settings_ = PrefHelper.getAll();
                        settings_.remove("token");
                        String json = new Gson().toJson(settings_);
                        String path =
                                Environment.getExternalStorageDirectory() + File.separator  + "FastHub";
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
                        }
                        catch (IOException e) {
                            Log.e(getTag(), "Couldn't backup: " + e.toString());
                        }

                        PrefHelper.set("backed_up", new SimpleDateFormat("MM/dd").format(new Date()));
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }

                    return true;
                });
                if(PrefHelper.getString("backed_up")!=null)
                    findPreference("backup").setSummary(getString(R.string.backup_summary, PrefHelper.getString("backed_up")));
                else
                    findPreference("backup").setSummary("");
                findPreference("restore").setOnPreferenceClickListener(preference -> {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        showFileChooser();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }

                    return true;
                });
                break;
            default:
                addPreferencesFromResource(R.xml.fasthub_settings);
                break;
        }
    }

    @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equalsIgnoreCase("notificationEnabled")) {
            if ((boolean)newValue) {
                getPreferenceScreen().addPreference(notificationTime);
                getPreferenceScreen().addPreference(notificationRead);
                getPreferenceScreen().addPreference(notificationSound);
                NotificationSchedulerJobTask.scheduleJob(getActivity().getApplicationContext(),
                        PrefGetter.notificationDurationMillis(getActivity().getApplicationContext(), PrefHelper.getString("notificationTime")), true);
            } else {
                getPreferenceScreen().removePreference(notificationTime);
                getPreferenceScreen().removePreference(notificationRead);
                getPreferenceScreen().removePreference(notificationSound);
                NotificationSchedulerJobTask.scheduleJob(getActivity().getApplicationContext(), -1, true);
            }
            return true;
        } else if (preference.getKey().equalsIgnoreCase("notificationTime")) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==PERMISSION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Map<String, ?> settings = PrefHelper.getAll();
                    settings.remove("token");
                    String json = new Gson().toJson(settings);
                    String path =
                            Environment.getExternalStorageDirectory() + File.separator  + "FastHub";
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
                    }
                    catch (IOException e) {
                        Log.e(getTag(), "Couldn't backup: " + e.toString());
                    }

                    PrefHelper.set("backed_up", new SimpleDateFormat("MM/dd").format(new Date()));
                    findPreference("backup").setSummary(getString(R.string.backup_summary, getString(R.string.now)));
                    Toasty.success(getContext(), getString(R.string.backed_up)).show();
                } else {
                    Toasty.error(getContext(), getString(R.string.permission_failed)).show();
                }
            } else if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser();
                } else {
                    Toasty.error(getContext(), getString(R.string.permission_failed)).show();
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==RESTORE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                StringBuilder json = new StringBuilder();
                try {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        json.append(line);
                    }
                    reader.close();
                    inputStream.close();
                }catch (IOException e) {
                    Toasty.error(getContext(), getString(R.string.error)).show();
                }

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(json.toString(), JsonObject.class);
                Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                for(Map.Entry<String,JsonElement> entry : entrySet){
                    if (entry.getValue().getAsJsonPrimitive().isBoolean())
                        PrefHelper.set(entry.getKey(), entry.getValue().getAsBoolean());
                    else if (entry.getValue().getAsJsonPrimitive().isNumber())
                        PrefHelper.set(entry.getKey(), entry.getValue().getAsNumber().intValue());
                    else if (entry.getValue().getAsJsonPrimitive().isString())
                        PrefHelper.set(entry.getKey(), entry.getValue().getAsString());
                    PrefHelper.set(entry.getKey(), entry.getValue());
                    Log.d(getTag(), entry.getKey() + ": " + entry.getValue());
                }
                callback.onThemeChanged();
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/json");

        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_backup)), RESTORE_REQUEST_CODE);
    }

}
