package smartlift.ibesk.ru.smartliftclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import smartlift.ibesk.ru.smartliftclient.services.ApiService;

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
        String ip = ipTv.getText().toString();
        if (TextUtils.isEmpty(ip)) return;
        String wsUrl = "ws://" + ip +":9000/clientSocket";
        mPrefs.edit().putString(ApiService.WS_URL, wsUrl).apply();
        startActivity(new Intent(this, MainActivity.class));

    }
}
