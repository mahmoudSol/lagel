package com.lagel.com.main.tab_fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/***Preference Class for save data of server***/
public class Lagel1Pref {
	private SharedPreferences prefs;


	private static final String isFilter = "lagel.filter";
	private static final String firstTime = "lagel.firstTime";


	public Lagel1Pref(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	}

	public Boolean getFirstTime() {
		return prefs.getBoolean(firstTime, false);
	}

	public void setFirstTime(Boolean value) {
		prefs.edit().putBoolean(firstTime, value).commit();
	}


	public Boolean getFilter() {
		return prefs.getBoolean(isFilter, false);
	}

	public void setFilter(Boolean value) {
		prefs.edit().putBoolean(isFilter, value).commit();
	}



}

