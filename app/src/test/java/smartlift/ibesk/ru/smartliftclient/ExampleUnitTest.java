package smartlift.ibesk.ru.smartliftclient;

import org.json.JSONException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGson() throws JSONException {
//        Gson gson = new Gson();
//        JsonReader reader = new JsonReader(new StringReader(JsonHolder.JSON_ARR));
//        reader.setLenient(true);
//        Question[] arr = gson.fromJson(reader, Question[].class);
//        assertNotNull(arr);
    }
}