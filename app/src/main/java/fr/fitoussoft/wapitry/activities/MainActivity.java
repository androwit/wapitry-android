package fr.fitoussoft.wapitry.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import fr.fitoussoft.wapisdk.IWapiApplication;
import fr.fitoussoft.wapisdk.activities.IWapiActivity;
import fr.fitoussoft.wapisdk.helpers.WapiClient;
import fr.fitoussoft.wapitry.R;

public class MainActivity extends Activity implements IWapiActivity {

    private void navigateToAccounts() {
        Intent myIntent = new Intent(MainActivity.this, AccountsActivity.class);
        MainActivity.this.startActivity(myIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("[TRY]", "Main onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
        Log.d("[TRY]", "Main onResume.");
        super.onResume();
        ((IWapiApplication) getApplication()).getWapiClient().verifyAuthentication(this);
    }

    @Override
    public void onAuthenticated(WapiClient wapiClient) {
        navigateToAccounts();
    }
}
