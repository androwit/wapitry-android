package fr.fitoussoft.wapisdk.activities;

import android.app.Application;

import fr.fitoussoft.wapisdk.IWapiApplication;
import fr.fitoussoft.wapisdk.helpers.WAPIClient;

/**
 * Created by emmanuel.fitoussi on 25/11/2014.
 */
public interface IWapiActivity {
    abstract void onAuthenticated(WAPIClient wapiClient);
}
