package smartlift.ibesk.ru.smartliftclient.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import android.view.View;
import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import smartlift.ibesk.ru.smartliftclient.model.api.ApiRequest;

import java.io.ByteArrayOutputStream;
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

    private static Activity mActivity;


    public static void start(Activity context) {
        mActivity = context;
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
                        if (req.getMethod().equals("screenshot"))
                            makeScreenshot();
                        else
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


    WebSocketClient imgSocket = null;
    private void makeScreenshot() {
        View v = mActivity.getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap bm = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        Log.d("qq", "makeScreenshot: ");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 20, bos);
        final byte[] byteArr = bos.toByteArray();
        try {
             imgSocket = new WebSocketClient(new URI("ws://192.168.2.40:9000/imgsocket")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(TAG, "Connected to img socket");
                    imgSocket.send("test");
//                    imgSocket.close();

                }

                @Override
                public void onMessage(String message) {
                    Log.d(TAG, "onMessage: " + message);

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
        } catch (URISyntaxException e) {
            Log.e(TAG, "makeScreenshot: ", e);
        }
    }
}
