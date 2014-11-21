package fr.fitoussoft.wapitry.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import fr.fitoussoft.wapisdk.helpers.WAPIClient;
import fr.fitoussoft.wapisdk.services.WAPIService;
import fr.fitoussoft.wapitry.R;

public class MainActivity extends Activity {

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("[TRY]", "onServiceConnected.");
            verifyTokenAndGoHome();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("[TRY]", "onServiceDisconnected.");
        }
    };

    private void navigateToAccounts() {
        Intent myIntent = new Intent(MainActivity.this, AccountsActivity.class);
        MainActivity.this.startActivity(myIntent);
    }

    private void verifyTokenAndGoHome() {

        WAPIClient client = WAPIService.getClient();
        if (client.hasToAuthenticate()) {
            WAPIClient.navigateToAuth(this);
            return;
        }

        navigateToAccounts();
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

        Intent intent = new Intent(this, WAPIService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        Log.d("[TRY]", "Main onPause.");
        super.onPause();
        unbindService(connection);
    }
}
