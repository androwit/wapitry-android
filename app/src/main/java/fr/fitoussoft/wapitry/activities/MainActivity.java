package fr.fitoussoft.wapitry.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import fr.fitoussoft.wapisdk.helpers.WAPIClient;
import fr.fitoussoft.wapisdk.services.WAPIServiceConnection;
import fr.fitoussoft.wapitry.R;

public class MainActivity extends Activity {

    private WAPIServiceConnection connection = new WAPIServiceConnection(this) {
        @Override
        public void onAuthenticated(WAPIClient client) {
            navigateToAccounts();
        }
    };

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
        connection.bindService();
    }

    @Override
    protected void onPause() {
        Log.d("[TRY]", "Main onPause.");
        super.onPause();
        connection.unbindService();
    }
}
