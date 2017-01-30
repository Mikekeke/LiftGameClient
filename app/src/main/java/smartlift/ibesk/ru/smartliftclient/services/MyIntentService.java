package smartlift.ibesk.ru.smartliftclient.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MyIntentService extends IntentService {

    public MyIntentService() {
        super("MyIntentService");
    }

    private static final String TAG = "qq";

    public static void start(Context context) {
        Intent in = new Intent(context, MyIntentService.class);
        context.startService(in);
    }

    WebSocketClient websc;
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
             websc = new WebSocketClient(new URI("ws://192.168.1.30:9000/socket")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(TAG, "onOpen: " + handshakedata.getHttpStatus());
                    Log.d(TAG, "onOpen: " + handshakedata.getHttpStatusMessage());
                    websc.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
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
//            websc.send("Test");
//            websc.close();

        } catch (URISyntaxException e) {
            Log.e(TAG, "onHandleIntent: ", e);
        }


//        Socket socket = null;
//        PrintWriter out = null;
//        BufferedReader in = null;
//        try {
//            socket = new Socket("http://localhost/socket", 9000);
//            out = new PrintWriter(socket.getOutputStream(), true);
//            in = new BufferedReader(
//                    new InputStreamReader(socket.getInputStream()));
//            Log.d("qq", "sending out");
//            out.println("test");
//            String line;
//            while ((line = in.readLine()) != null) {
//                Log.d("qq", "in: " + line);
//            }
//        } catch (IOException e) {
//            Log.e("qq", "onHandleIntent: ", e);
//        } finally {
//            if (socket != null) {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if (out != null) {
//                out.close();
//            }
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            Log.d("qq", "onHandleIntent: " + "CLOSING");
//        }
    }
}
