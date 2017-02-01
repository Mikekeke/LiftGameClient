package smartlift.ibesk.ru.smartliftclient.utils;

/**
 * Created by Mikekeke on 27-Jan-17.
 */

public final class JsonHolder {
    public static final String JSON1 =
            "{\"num\":1,\"name\":\"Вопрос #1\",\"question\":\"Кто такой вопрос 1?\",\"variants\":{\"1\":{\"num\":\"1\",\"text\":\"вариант ответа 1\"},\"2\":{\"num\":\"2\",\"text\":\"вариант ответа 2\"},\"3\":{\"num\":\"3\",\"text\":\"вариант ответа 3 - правильный\"},\"4\":{\"num\":\"4\",\"text\":\"вариант ответа 4\"}},\"correctVar\":3,\"answer\":\"Ответ на вопрос 1\",\"status\":{\"value\":-1},\"ing1\":\"\",\"img2\":\"\"}";

    public static final String JSON2 =
            "{\"num\":2,\"name\":\"Вопос #2\",\"question\":\"Что такое вопрос 2?\",\"variants\":{\"1\":{\"num\":\"1\",\"text\":\"вариант ответа 1\"},\"2\":{\"num\":\"2\",\"text\":\"вариант ответа 2 - правильный\"},\"3\":{\"num\":\"3\",\"text\":\"вариант ответа 3\"},\"4\":{\"num\":\"4\",\"text\":\"вариант ответа 4\"}},\"correctVar\":2,\"answer\":\"Ответ на порос два - это ответ на вопрос 2\",\"status\":{\"value\":-1},\"ing1\":\"\",\"img2\":\"\"}";


    public static final String[] JSON_ARR = {JSON1, JSON2};

    private static int cnt = 0;
    public static String getQuestion() {
        String j = JSON_ARR[cnt];
        cnt++;
        if (cnt >= JSON_ARR.length) cnt = 0;
        return j;
    }
}
