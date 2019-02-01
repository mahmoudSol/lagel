package com.lagel.com.service;



import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ActionIntent extends Intent {

	public ActionIntent(String action, Context packageContext, Class<?> cls) {
		super(packageContext, cls);
		this.setAction(action);
	}

	public ActionIntent(String action, Uri uri, Context packageContext,
                        Class<?> cls) {
		super(action, uri, packageContext, cls);
	}

	public ActionIntent(String action, Uri uri) {
		super(action, uri);
	}

	public ActionIntent(String action) {
		super(action);
	}
}