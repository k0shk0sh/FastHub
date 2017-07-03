package com.fastaccess.data.dao;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by JediB on 5/12/2017.
 */

@Getter @Setter @AllArgsConstructor public class SettingsModel {


    public static final int THEME = 0;
    public static final int NOTIFICATION = 1;
    public static final int BEHAVIOR = 2;
    public static final int CUSTOMIZATION = 3;
    public static final int BACKUP = 4;
    public static final int LANGUAGE = 5;
    public static final int CODE_THEME = 6;

    @IntDef({
            THEME,
            NOTIFICATION,
            CUSTOMIZATION,
            BEHAVIOR,
            BACKUP,
            LANGUAGE,
            CODE_THEME
    })
    @Retention(RetentionPolicy.SOURCE) public @interface SettingsType {}

    private int image;
    private String title;
    @SettingsType private int settingsType;
}
