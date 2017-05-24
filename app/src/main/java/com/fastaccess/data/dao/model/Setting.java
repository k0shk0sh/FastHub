package com.fastaccess.data.dao.model;

import android.graphics.drawable.Drawable;

/**
 * Created by JediB on 5/12/2017.
 */

public class Setting {

	private int image;
	private String title;
	private String summary;

	public static Setting newInstance(int icon, String title, String summary) {
		Setting setting = new Setting();
		setting.image = icon;
		setting.title = title;
		setting.summary = summary;

		return setting;
	}

	public int getImage(){
		return image;
	}

	public String getTitle(){
		return title;
	}

	public String getSummary(){
		return summary;
	}

}
