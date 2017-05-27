package com.fastaccess.data.dao;

/**
 * Created by JediB on 5/12/2017.
 */

public class SettingsModel {

	private int image;
	private String title;
	private String summary;

	public static SettingsModel newInstance(int icon, String title, String summary) {
		SettingsModel setting = new SettingsModel();
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
