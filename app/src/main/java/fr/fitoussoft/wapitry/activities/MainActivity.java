package fr.fitoussoft.wapitry.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import fr.fitoussoft.wapitry.R;
import fr.fitoussoft.wapitry.helpers.WAPIClient;

public class MainActivity extends Activity {

    private static WAPIClient _client;

    public static WAPIClient getClient() {
        return _client;
    }

    private void navigateToAccounts() {
        Intent myIntent = new Intent(MainActivity.this, AccountsActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }

    private void verifyTokenAndGoHome() {
        if (MainActivity.getClient().hasRefreshToken()) {
            navigateToAccounts();
            return;
        }

        _client.navigateToAuth(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("[TRY]", "Main onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (_client == null) {
            // setups WAPIClient.
            _client = new WAPIClient(this);
        }

    }

    @Override
    protected void onResume() {
        Log.d("[TRY]", "Main onResume.");
        super.onResume();
        verifyTokenAndGoHome();
    }
}
