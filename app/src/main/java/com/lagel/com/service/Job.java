package com.lagel.com.service;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.SystemClock;

public abstract class Job implements Runnable {
	private AlarmManager alarmManager;
	private PendingIntent intent;
	private long interval;
	
	public Job(AlarmManager alarmManager, PendingIntent pendingIntent, long interval) {
		this.alarmManager = alarmManager;
		this.intent = pendingIntent;
		this.interval = interval;
	}
	
	public void start() {
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,	SystemClock.elapsedRealtime(), interval, intent);
	}
	
	public void stop() {
		alarmManager.cancel(intent);
	}
	
	public void start2() {
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, interval, AlarmManager.INTERVAL_DAY,intent);
		//alarmManager.setRepeating(AlarmManager.INTERVAL_DAY,SystemClock.elapsedRealtime(), interval, intent);
	}

}
