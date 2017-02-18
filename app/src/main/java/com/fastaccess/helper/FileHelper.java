package com.fastaccess.helper;

import android.os.Environment;
import android.webkit.MimeTypeMap;

/**
 * Created by kosh20111 on 10/7/2015. CopyRights @ Innov8tif
 */
public class FileHelper {
    public static String getExtension(String file) {
        return MimeTypeMap.getFileExtensionFromUrl(file);
    }

    public static String getDownloadDirectory() {
        return Environment.DIRECTORY_DOWNLOADS;
    }
}
