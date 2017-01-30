package smartlift.ibesk.ru.smartliftclient.utils;

/**
 * Created by Mikekeke on 27-Jan-17.
 */

public final class JsonHolder {
    public static final String JSON1 =
            "{\"num\":1,\"name\":\"Question #1\",\"question\":\"What is question one?\",\"variants\":{\"1\":{\"num\":\"1\",\"text\":\"first var\"},\"2\":{\"num\":\"2\",\"text\":\"second var\"},\"3\":{\"num\":\"3\",\"text\":\"third var - correct\"},\"4\":{\"num\":\"4\",\"text\":\"fourth var\"}},\"correctVar\":3,\"answer\":\"answer to question one\",\"status\":{\"value\":-1},\"ing1\":\"\",\"img2\":\"\"}";

    public static final String JSON2 =
            "{\"num\":2,\"name\":\"Question #2\",\"question\":\"Who is question two?\",\"variants\":{\"1\":{\"num\":\"1\",\"text\":\"first var\"},\"2\":{\"num\":\"2\",\"text\":\"second var - correct\"},\"3\":{\"num\":\"3\",\"text\":\"third var\"},\"4\":{\"num\":\"4\",\"text\":\"fourth var\"}},\"correctVar\":2,\"answer\":\"answer to question TWO\",\"status\":{\"value\":-1},\"ing1\":\"\",\"img2\":\"\"}";


    public static final String[] JSON_ARR = {JSON1, JSON2};

    private static int cnt = 0;
    public static String getQuestion() {
        String j = JSON_ARR[cnt];
        cnt++;
        if (cnt >= JSON_ARR.length) cnt = 0;
        return j;
    }
}
