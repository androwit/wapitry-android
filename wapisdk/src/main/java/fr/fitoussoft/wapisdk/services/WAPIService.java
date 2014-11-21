package fr.fitoussoft.wapisdk.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import fr.fitoussoft.wapisdk.helpers.Log;
import fr.fitoussoft.wapisdk.helpers.WAPIClient;

public class WAPIService extends Service {

    private WAPIClient _client;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        _client = new WAPIClient(this, prefs);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("onBind");
        return _client;
    }

}
