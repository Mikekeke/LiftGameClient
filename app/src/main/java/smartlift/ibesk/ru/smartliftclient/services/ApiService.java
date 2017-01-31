package smartlift.ibesk.ru.smartliftclient.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import smartlift.ibesk.ru.smartliftclient.model.api.ApiRequest;

import java.net.URI;
import java.net.URISyntaxException;

import static smartlift.ibesk.ru.smartliftclient.model.api.Api.ACTION.*;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.*;

public class ApiService extends IntentService {
    public static final String EXTRA_METHOD = "ApiService.EXTRA_METHOD";
    public static final String EXTRA_CONTENT = "ApiService.EXTRA_CONTENT";

    public ApiService() {
        super("ApiService");
    }

    private static final String TAG = "qq";
    private static final Gson GSON = new Gson();


    public static void start(Context context) {
        Intent in = new Intent(context, ApiService.class);
        context.startService(in);
    }

    WebSocketClient websc;

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
//             websc = new WebSocketClient(new URI("ws://192.168.1.30:9000/socket")) {
            websc = new WebSocketClient(new URI("ws://192.168.2.40:9000/socket")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(TAG, "onOpen: " + handshakedata.getHttpStatus());
                    Log.d(TAG, "onOpen: " + handshakedata.getHttpStatusMessage());
                    websc.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                }

                @Override
                public void onMessage(String message) {
                    Log.d(TAG, "onMessage: " + message);
                    try {
//                        JsonReader reader = new JsonReader(new StringReader(message));
//                        reader.setLenient(true);
                        ApiRequest req = GSON.fromJson(message, ApiRequest.class);
                        broadcast(req.getMethod(), req.getContent());
                    } catch (Exception e) {
                        Log.w("qq", "onMessage: ain't parsable: " + message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "onClose: ");

                }

                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "onError: ", ex);

                }
            };
            websc.connect();
//            websc.send("Test");
//            websc.close();

        } catch (URISyntaxException e) {
            Log.e(TAG, "onHandleIntent: ", e);
        }
    }
    private Intent mBroadcastIntent = new Intent(API_ACTION);
    private void broadcast(String method, String content) {
        mBroadcastIntent.putExtra(EXTRA_METHOD, method);
        mBroadcastIntent.putExtra(EXTRA_CONTENT, content);
        LocalBroadcastManager.getInstance(this).sendBroadcast(mBroadcastIntent);
    }
}
