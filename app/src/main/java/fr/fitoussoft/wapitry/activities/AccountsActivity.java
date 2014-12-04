package fr.fitoussoft.wapitry.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.fitoussoft.wapisdk.models.Account;
import fr.fitoussoft.wapisdk.tasks.RequestBusinessAccountsAsyncTask;
import fr.fitoussoft.wapisdk.tasks.RequestPictureAsyncTask;
import fr.fitoussoft.wapitry.Application;
import fr.fitoussoft.wapitry.R;

public class AccountsActivity extends Activity {
    private List<Account> accounts;
    private ArrayAdapter<Account> accountsAdapter;
    private ProgressBar progressBar;

    private void displayAccounts() {
        accounts.clear();
        RequestBusinessAccountsAsyncTask task = new RequestBusinessAccountsAsyncTask(this) {
            @Override
            protected void onPostExecute(List<Account> accounts) {
                if (accounts != null) {
                    accountsAdapter.addAll(accounts);
                }

                RequestPictureAsyncTask task;
                for (final Account account : accounts) {
                    task = new RequestPictureAsyncTask(AccountsActivity.this) {

                        @Override
                        protected void onPostExecute(byte[] pictureBytes) {
                            account.setPictureBytes(pictureBytes);
                            Bitmap pictureBitmap = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);
                            account.setPictureBitmap(pictureBitmap);
                            accountsAdapter.notifyDataSetChanged();
                        }
                    };

                    task.getParams().put(RequestPictureAsyncTask.PARAM_ID, account.getPicture());
                    task.getParams().put(RequestPictureAsyncTask.PARAM_SIZE, AccountsActivity.this.getResources().getString(fr.fitoussoft.wapisdk.R.string.icon_size));
                    task.execute();
                }

                progressBar.setVisibility(View.INVISIBLE);
            }
        };

        progressBar.setVisibility(View.VISIBLE);
        task.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("[TRY]", "Accounts onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts);

        if (progressBar == null) {
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
        }

        progressBar.setVisibility(View.INVISIBLE);

        if (accounts == null) {
            accounts = new ArrayList<Account>();
        }

        if (accountsAdapter == null) {
            accountsAdapter = new ArrayAdapter<Account>(AccountsActivity.this, R.layout.account_item, accounts) {

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
                    ImageView ivPicture = (ImageView) convertView.findViewById(R.id.picture);

                    // Populate the data into the template view using the data object
                    tvName.setText(account.getName());
                    tvWac.setText(account.getWac());
                    ivPicture.setImageBitmap(account.getPictureBitmap());

                    // Return the completed view to render on screen
                    return convertView;
                }

            };
        }

        ListView listView = (ListView) findViewById(R.id.accounts);
        listView.setAdapter(accountsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent appInfo = new Intent(AccountsActivity.this, ReflectionsActivity.class);
                appInfo.putExtra("wac", accounts.get(position).getWac()); //Optional parameters
                startActivity(appInfo);
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d("[TRY]", "Accounts onResume.");
        super.onResume();
        displayAccounts();
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
            ((Application) getApplication()).getWapiClient().disconnect(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
