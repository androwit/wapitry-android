package fr.fitoussoft.wapisdk.activities;

import fr.fitoussoft.wapisdk.helpers.WapiClient;

/**
 * Created by emmanuel.fitoussi on 25/11/2014.
 */
public interface IWapiActivity {
    abstract void onAuthenticated(WapiClient wapiClient);
}
