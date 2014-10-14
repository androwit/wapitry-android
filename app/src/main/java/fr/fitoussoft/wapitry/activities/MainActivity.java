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

    public void navigateToAccounts() {
        Intent myIntent = new Intent(MainActivity.this, AccountsActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }

    public void navigateToAuth() {
        Intent myIntent = new Intent(MainActivity.this, AuthActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        MainActivity.this.startActivity(myIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // setup WAPI client.
        _client = new WAPIClient(this);

        if (_client.hasAccessToken() && !_client.hasExpired()) {
            Log.d("[TRY]", "has an access token.");
            // goto accounts
            this.navigateToAccounts();
            return;
        } else if (_client.hasExpired() && _client.hasRefreshToken() && _client.refreshAccess()) {
            // goto accounts
            this.navigateToAccounts();
            return;
        }

        this.navigateToAuth();
    }
}
