package fr.fitoussoft.wapitry.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import fr.fitoussoft.wapisdk.activities.AuthActivity;
import fr.fitoussoft.wapisdk.helpers.WAPIClient;
import fr.fitoussoft.wapitry.R;

public class MainActivity extends Activity {

    private void navigateToAccounts() {
        Intent myIntent = new Intent(MainActivity.this, AccountsActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }

    private void verifyTokenAndGoHome() {
        WAPIClient client = AuthActivity.setupWAPIClient(this);
        if (client.hasRefreshToken()) {
            navigateToAccounts();
            return;
        }

        client.navigateToAuth(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("[TRY]", "Main onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        AuthActivity.setupWAPIClient(this);
    }

    @Override
    protected void onResume() {
        Log.d("[TRY]", "Main onResume.");
        super.onResume();
        verifyTokenAndGoHome();
    }
}
