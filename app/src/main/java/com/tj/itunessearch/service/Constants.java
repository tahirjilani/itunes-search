package com.tj.itunessearch.service;

public class Constants {
    public interface ACTION {
        public static String NOTIFICATION_CLICKED_ACTION = "com.tj.itunessearch.service.notification_clicked";
        public static String START_ACTION = "com.tj.itunessearch.service.start";
        public static String PREV_ACTION = "com.tj.itunessearch.service.prev";
        public static String PLAY_PAUSE_ACTION = "com.tj.itunessearch.service.play_pause";
        public static String NEXT_ACTION = "com.tj.itunessearch.service.next";
        public static String STOP_ACTION = "com.tj.itunessearch.service.stop";
        public static String CLEAR_ACTION = "com.tj.itunessearch.service.clear";

    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 109;
        public static String CHANNEL_ID = "14";

    }
}
