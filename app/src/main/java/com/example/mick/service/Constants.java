package com.example.mick.service;

/**
 * Created by Mick on 27.04.2017.
 */
public class Constants {
    public interface ACTION {
       /* public static String MAIN_ACTION = "com.truiton.foregroundservice.action.main";
        public static String PREV_ACTION = "com.truiton.foregroundservice.action.prev";
        public static String PLAY_ACTION = "com.truiton.foregroundservice.action.play";
        public static String NEXT_ACTION = "com.truiton.foregroundservice.action.next";
        public static String STARTFOREGROUND_ACTION = "com.truiton.foregroundservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.truiton.foregroundservice.action.stopforeground";*/
    }

    public interface ANALIZATION{

        /**
         * Key for intend, when creating a new diagramm activity
         */
        public static String DIAGRAM_MODE = "DIAGRAM_MODE";

        /**
         * Value for the diagram mode - means that the analization is currently running
         */
        public static String MODE_ANALIZATION_RUNNING = "MODE_ANALIZATION_RUNNING";

        /**
         * Value for diagramm mode -> meaning is, that the result of the last analisation should be shown,
         * and the stop button should be hidden
         */
        public static String MODE_ANALIZATION_STOPPED = "MODE_ANALIZATION_STOPPED";

        /**
         * Value for the diagramm mode - show history, additional value (MODE_HISTORY_DATE) is needed.
         * and the stop button should be hidden
         */
        public static String MODE_HISTORY = "MODE_HISTORY";

        /**
         * should be used to put the date (used as id) to the diagramm intent
         */
        public static String MODE_HISTORY_DATE = "MODE_HISTORY_DATE";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}