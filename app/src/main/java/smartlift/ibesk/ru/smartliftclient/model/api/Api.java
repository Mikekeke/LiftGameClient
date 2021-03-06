package smartlift.ibesk.ru.smartliftclient.model.api;

/**
 * Created by ibes on 31.01.17.
 */
public final class Api {
    public static final class SOCKET {
        public static final String UP = "ON AIR";
        public static final String DOWN = "OFFLINE";
    }

    public static final class METHOD {
        public static final String QUESTION = "question";
        public static final String CHECK = "check";
        public static final String LOGO = "logo";
        public static final String PICK_VARIANT = "pick_variant";
        public static final String EXPAND_ANSWER = "expand_answer";
        public static final String SCREENSHOT = "screenshot";
        public static final String STATUS = "status";
        public static final String TIMER_SET = "TIMER_SET";
        public static final String TIMER_START = "TIMER_START";
        public static final String TIMER_STOP = "TIMER_STOP";
        public static final String TIMER_RESET = "TIMER_RESET";

    }

    public static final class ACTION {
        public static final String API_ACTION = "api_action";
//        public static final String LOAD_QUESTION = "load_question";
//        public static final String CHECK_QUESTION = "check_question";
    }
}
