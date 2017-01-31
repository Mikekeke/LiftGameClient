package smartlift.ibesk.ru.smartliftclient.model.api;

/**
 * Created by ibes on 31.01.17.
 */
public class ApiRequest {
    private String method = "";

    public String getMethod() {
        return method;
    }

    public String getContent() {
        return content;
    }

    private String content = "";

}
