package smartlift.ibesk.ru.smartliftclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import smartlift.ibesk.ru.smartliftclient.model.api.Api;

public class StartActivity extends AppCompatActivity {
    private SharedPreferences mPrefs;
    private TextView ipTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ipTv = (TextView) findViewById(R.id.ip_field);
    }

    public void setIp(View view) {
        if (ipTv == null) return;
        String ip_port = ipTv.getText().toString();
        if (TextUtils.isEmpty(ip_port)) return;
        String wsUrl = "ws://" + ip_port +"/clientSocket";
        mPrefs.edit().putString(Settings.WS_URL, wsUrl).apply();
        String imagesDl = "http://" + ip_port + Api.IMAGES;
        mPrefs.edit().putString(Settings.IMG_URL, imagesDl).apply();
        startActivity(new Intent(this, MainActivity.class));

    }
}
