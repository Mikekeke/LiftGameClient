package smartlift.ibesk.ru.smartliftclient.sockets;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CountDownLatch;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import de.tavendo.autobahn.WebSocketOptions;

/**
 * Created by ibes on 01.02.17.
 */
public class ImageSocket extends WebSocketConnection {
    private static final String TAG = "qq";
    private CountDownLatch latch = new CountDownLatch(1);

    private WebSocketHandler mHandler = new WebSocketHandler() {
        @Override
        public void onOpen() {
            super.onOpen();
            latch.countDown();
            Log.d(TAG, "ImgSocket onOpen: ");
        }

        @Override
        public void onClose(int code, String reason) {
            super.onClose(code, reason);
            Log.d(TAG, "ImgSocket onClose: " + code + " - " + reason);
        }

        @Override
        public void onBinaryMessage(byte[] payload) {
            super.onBinaryMessage(payload);
            try {
                Log.d(TAG, "onBinaryMessage: " + new String(payload, 0, payload.length, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "onBinaryMessageError: ", e);
            }
        }
    };


    public void connect(String wsUri) throws WebSocketException {
        WebSocketOptions options = new WebSocketOptions();
        options.setSocketConnectTimeout(2000);
        options.setMaxFramePayloadSize(10000000);
        options.setMaxMessagePayloadSize(10000000);
        super.connect(wsUri, mHandler, options);
//        super.connect(wsUri, wsHandler, options);
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
