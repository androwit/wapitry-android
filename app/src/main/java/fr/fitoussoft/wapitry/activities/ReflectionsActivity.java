package fr.fitoussoft.wapitry.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import fr.fitoussoft.wapitry.R;
import fr.fitoussoft.wapitry.helpers.WAPIClient;
import fr.fitoussoft.wapitry.models.Reflection;

public class ReflectionsActivity extends Activity {

    private List<Reflection> reflections;
    private String wac;
    private ArrayAdapter<Reflection> reflectionsAdapter;
    private AsyncTask<String,Integer,List<Reflection>> requestReflections;
    private ProgressBar progressBar;



    private AsyncTask<String,Integer,List<Reflection>> createRequestAsyncTask() {
        return new AsyncTask<String,Integer,List<Reflection>>() {
            @Override
            protected List<Reflection> doInBackground(String... strings) {
                WAPIClient client = MainActivity.getClient();
                return client.requestReflections(strings[0]);
            }

            @Override
            protected void onPostExecute(List<Reflection> list) {
                if(list != null) {
                    reflectionsAdapter.clear();
                    reflectionsAdapter.addAll(list);
                }

                progressBar.setVisibility(View.INVISIBLE);
            }
        };
    }

    private void navigateToAuth() {
        Intent myIntent = new Intent(ReflectionsActivity.this, AuthActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        ReflectionsActivity.this.startActivity(myIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("[TRY]", "Reflections onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflections);

        wac = getIntent().getExtras().getString("wac");

        if(progressBar == null) {
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
        }

        progressBar.setVisibility(View.INVISIBLE);

        if (reflections == null) {
            WAPIClient client = MainActivity.getClient();
            if (!client.hasAccessToken() && client.hasExpired() && (!client.hasRefreshToken() || !client.refreshAccess())) {
                navigateToAuth();
                return;
            }

            //reflections = client.requestReflections(wac);
            reflections = new ArrayList<Reflection>();
        }

        if (reflectionsAdapter == null) {
            reflectionsAdapter = new ArrayAdapter<Reflection>(this, R.layout.reflection_item, reflections) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Get the data item for this position
                    Reflection reflection = getItem(position);

                    // Check if an existing view is being reused, otherwise inflate the view
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.reflection_item, parent, false);
                    }

                    // Lookup view for data population
                    TextView tvName = (TextView) convertView.findViewById(R.id.name);
                    TextView tvClassName = (TextView) convertView.findViewById(R.id.className);

                    // Populate the data into the template view using the data object
                    tvName.setText(reflection.getName());
                    tvClassName.setText(reflection.getClassName());

                    // Return the completed view to render on screen
                    return convertView;
                }
            };
        }

        ListView listView = (ListView) findViewById(R.id.reflections);
        listView.setAdapter(reflectionsAdapter);

        if(requestReflections == null) {
            requestReflections = createRequestAsyncTask();
        }

        progressBar.setVisibility(View.VISIBLE);
        requestReflections.execute(wac);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reflections, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
