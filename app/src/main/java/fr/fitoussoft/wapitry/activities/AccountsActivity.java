package fr.fitoussoft.wapitry.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import fr.fitoussoft.wapitry.R;
import fr.fitoussoft.wapitry.helpers.WAPIClient;
import fr.fitoussoft.wapitry.models.Account;

public class AccountsActivity extends Activity {

    private List<Account> accounts;

    private ArrayAdapter<Account> accountsAdapter;

    private void disconnect() {
        Log.d("[TRY]", "disconnect.");
        MainActivity.getClient().resetTokens();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        this.navigateToAuth();
    }

    private void navigateToAuth() {
        Intent myIntent = new Intent(AccountsActivity.this, AuthActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        AccountsActivity.this.startActivity(myIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("[TRY]", "Accounts onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts);

        if (accounts == null) {
            WAPIClient client =  MainActivity.getClient();
            if (!client.hasAccessToken() && client.hasExpired() && (!client.hasRefreshToken() || !client.refreshAccess())) {
                navigateToAuth();
                return;
            }

            accounts = client.requestBusinessAccounts();
        }

        if (accountsAdapter == null) {
            accountsAdapter = new ArrayAdapter<Account>(this, R.layout.account_item, accounts) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Get the data item for this position
                    Account account = getItem(position);
                    // Check if an existing view is being reused, otherwise inflate the view
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.account_item, parent, false);
                    }
                    // Lookup view for data population
                    TextView tvName = (TextView) convertView.findViewById(R.id.name);
                    TextView tvWac = (TextView) convertView.findViewById(R.id.wac);
                    // Populate the data into the template view using the data object
                    tvName.setText(account.getName());
                    tvWac.setText(account.getWac());
                    // Return the completed view to render on screen
                    return convertView;
                }
            };
        }

        ListView listView = (ListView) findViewById(R.id.accounts);
        listView.setAdapter(accountsAdapter);
    }

    @Override
    protected void onResume() {
        Log.d("[TRY]", "Accounts onResume.");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.accounts, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.option_disconnect) {
            this.disconnect();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
