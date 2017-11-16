package com.fastaccess.helper;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.R;
import com.fastaccess.data.dao.NotificationSoundModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kosh20111 on 10/7/2015. CopyRights @
 */
public class FileHelper {

    public static final String PATH = Environment.getExternalStorageDirectory() + File.separator + "FastHub";

    public static final long ONE_MB = 1048576L;

    @Nullable public static String getPath(@NonNull Context context, @NonNull Uri uri) {
        String filePath = null;
        try {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";
            try (Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null)) {
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndex(column[0]);
                    if (cursor.moveToFirst()) {
                        filePath = cursor.getString(columnIndex);
                    }
                }
            }
        } catch (Exception ignored) {}
        return filePath;
    }

    @NonNull public static String getRingtoneName(@NonNull Context context, @Nullable Uri uri) {
        String title = context.getString(R.string.sound_chooser_summary);
        if (uri != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
            if (ringtone != null) {
                return ringtone.getTitle(context);
            } else {
                try (Cursor cur = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Media.TITLE}, MediaStore.Audio.Media._ID + " =?",
                        new String[]{uri.getLastPathSegment()}, null)) {
                    if (cur != null) {
                        title = cur.getString(1);
                        if (InputHelper.isEmpty(title)) {
                            title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
        return title;
    }

    public static ArrayList<NotificationSoundModel> getNotificationSounds(Context context, @Nullable String defaultValue) {
        ArrayList<NotificationSoundModel> notificationSounds = new ArrayList<>();
        RingtoneManager ringtoneManager = new RingtoneManager(context);
        ringtoneManager.setType(RingtoneManager.TYPE_NOTIFICATION);
        try (Cursor ringsCursor = ringtoneManager.getCursor()) {
            while (ringsCursor.moveToNext()) {
                String title = ringsCursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                Uri uri = Uri.parse(ringsCursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/"
                        + ringsCursor.getString(RingtoneManager.ID_COLUMN_INDEX));
                boolean selected = defaultValue != null && (uri.toString().contains(defaultValue) ||
                        title.equalsIgnoreCase(defaultValue) || defaultValue.contains(title));
                Logger.e(defaultValue, title, uri, selected);
                notificationSounds.add(new NotificationSoundModel(title, uri, selected));
            }
        }
        return notificationSounds;
    }


}
