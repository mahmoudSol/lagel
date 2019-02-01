package com.lagel.com.fcm_push_notification;

/**
 * <h>Config</h>
 * <p>
 *     In this class we used to save some Notification related contant variables.
 * </p>
 */
public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    static final int NOTIFICATION_ID = 100;
    static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
}
