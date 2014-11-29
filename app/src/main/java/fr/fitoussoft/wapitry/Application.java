package fr.fitoussoft.wapitry;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import fr.fitoussoft.wapisdk.IWapiApplication;
import fr.fitoussoft.wapisdk.helpers.WAPIClient;

/**
 * Created by emmanuel.fitoussi on 24/11/2014.
 */
public class Application extends android.app.Application implements IWapiApplication {

    private WAPIClient wapiClient;

    @Override
    public WAPIClient getWapiClient() {
        return wapiClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        wapiClient = new WAPIClient(this, prefs);
    }
}
