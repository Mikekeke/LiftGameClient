package smartlift.ibesk.ru.smartliftclient.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import de.tavendo.autobahn.WebSocketOptions;
import smartlift.ibesk.ru.smartliftclient.model.api.Api;
import smartlift.ibesk.ru.smartliftclient.model.api.ApiRequest;
import smartlift.ibesk.ru.smartliftclient.sockets.ImageSocket;

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


    private ImageSocket mImgSocket;


    public static void start(Activity context) {
        Intent in = new Intent(context, ApiService.class);
        context.startService(in);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
//        imgSocket();
//        stringSocket();
        try {
            autbnSocket();
            Log.d(TAG, "ApiService onHandleIntent: ");
        } catch (WebSocketException e) {
            Log.e(TAG, "Autobahn socket error: ", e);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ApiService onDestroy: ");
    }

    private Intent mBroadcastIntent = new Intent(API_ACTION);

    private void broadcast(String method, String content) {
        mBroadcastIntent.putExtra(EXTRA_METHOD, method);
        mBroadcastIntent.putExtra(EXTRA_CONTENT, content);
        LocalBroadcastManager.getInstance(this).sendBroadcast(mBroadcastIntent);
    }



    private static final WebSocketConnection CONNECTION = new WebSocketConnection();

    private void autbnSocket() throws WebSocketException {
        if (!CONNECTION.isConnected()) {
            WebSocketOptions options = new WebSocketOptions();
            options.setSocketConnectTimeout(2000);
            options.setMaxFramePayloadSize(1000000);
            options.setMaxMessagePayloadSize(1000000);
            CONNECTION.connect("ws://192.168.2.40:9000/socket", handler, options);
        }
    }

    private WebSocketHandler handler = new WebSocketHandler() {
        @Override
        public void onOpen() {
            super.onOpen();

            Log.d(TAG, "onOpen: ");
            broadcast(STATUS, Api.SOCKET.UP);
            CONNECTION.sendTextMessage("test");
        }

        @Override
        public void onClose(int code, String reason) {
            super.onClose(code, reason);
            broadcast(STATUS, Api.SOCKET.DOWN);
            Log.d(TAG, "onClose: " + "code" + " - " + reason);
        }

        @Override
        public void onTextMessage(String message) {
            super.onTextMessage(message);
            Log.d(TAG, "onMessage: " + message);
            try {
//                        JsonReader reader = new JsonReader(new StringReader(message));
//                        reader.setLenient(true);
                ApiRequest req = GSON.fromJson(message, ApiRequest.class);
                if (req.getMethod().equals("screenshot"))
//                    makeScreenshot();
                    Log.d(TAG, "Screenshot");
                else
                    broadcast(req.getMethod(), req.getContent());
            } catch (Exception e) {
                Log.w("qq", "onMessage: ain't parsable: " + message);
            }
        }

        @Override
        public void onRawTextMessage(byte[] payload) {
            super.onRawTextMessage(payload);
            Log.d(TAG, "onRawTextMessage: ");
        }

        @Override
        public void onBinaryMessage(byte[] payload) {
            super.onBinaryMessage(payload);

        }
    };

    private void makeScreenshot() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mImgSocket == null) {
                    mImgSocket = new ImageSocket();
                }
                if (!mImgSocket.isConnected())
                    try {
                        mImgSocket.connect("ws://192.168.2.40:9000/imgsocket");
                    } catch (WebSocketException e) {
                        Log.e(TAG, "run: ", e);
                    }
                try {
                    mImgSocket.getLatch().await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                mImgSocket.sendBinaryMessage(getImgAsBytes());
            }
        }).start();

    }

//    private byte[] getImgAsBytes() {
//        View v = mActivity.getWindow().getDecorView().getRootView();
//        v.setDrawingCacheEnabled(true);
//        Bitmap bm = Bitmap.createBitmap(v.getDrawingCache());
//        v.setDrawingCacheEnabled(false);
//        Log.d("qq", "makeScreenshot: ");
//        Bitmap resizedBm = Bitmap.createScaledBitmap(bm, bm.getWidth() / 7, bm.getHeight() / 7, false);
//        bm.recycle();
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        resizedBm.compress(Bitmap.CompressFormat.PNG, 20, bos);
//        return bos.toByteArray();
//    }

    public static void closeConnection() {
    }
}
