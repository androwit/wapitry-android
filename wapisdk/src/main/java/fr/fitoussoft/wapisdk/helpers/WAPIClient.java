package fr.fitoussoft.wapisdk.helpers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.StrictMode;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.widget.Toast;

import fr.fitoussoft.wapisdk.activities.AuthActivity;
import fr.fitoussoft.wapisdk.activities.IWapiActivity;
import fr.fitoussoft.wapisdk.tasks.RequestRefreshAccessTokenAsyncTask;


/**
 * Created by emmanuel.fitoussi on 07/10/2014.
 */
public class WapiClient {
    public static boolean DEBUG = true;
    public int nextSkipReflectionRequest = 0;
    private Configuration config;

    private Context context;
    private WapiToken token;

    public WapiClient(Context context, SharedPreferences prefs) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.context = context;
        Resources res = this.context.getResources();

        config = new Configuration(res, DEBUG);

        // restore stored tokens
        token = new WapiToken(prefs, DEBUG);
    }

    private static void navigateToAuth(Activity activity) {
        Intent myIntent = new Intent(activity, AuthActivity.class);
        activity.startActivityForResult(myIntent, 0);
    }

    public WapiToken getToken() {
        return token;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void disconnect(final Activity activity) {
        Log.d("disconnect.");
        token.resetTokens();
        Log.d("SDK: " + Build.VERSION.SDK_INT + ", JELLY BEAN: " + Build.VERSION_CODES.KITKAT);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.flush();
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {

                @Override
                public void onReceiveValue(Boolean aBoolean) {
                    WapiClient.navigateToAuth(activity);
                }
            });
        } else {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            WapiClient.navigateToAuth(activity);
        }
    }

    public Configuration getConfig() {
        return config;
    }

    public boolean hasToAuthenticate() {
        return !token.hasRefreshToken();
    }

    public boolean hasToRefreshAccessToken() {
        return !token.hasAccessToken() || token.hasExpired();
    }

    public Context getContext() {
        return context;
    }

    public void verifyAuthentication(final Activity origin) {
        if (hasToAuthenticate()) {
            navigateToAuth(origin);
            return;
        }

        final IWapiActivity originActivity = (IWapiActivity) origin;

        if (hasToRefreshAccessToken()) {
            RequestRefreshAccessTokenAsyncTask task = new RequestRefreshAccessTokenAsyncTask(this) {
                @Override
                protected void onPostExecute(Boolean result) {
                    originActivity.onAuthenticated(WapiClient.this);
                }
            };
            task.execute();
        }

        originActivity.onAuthenticated(this);
    }

    public void logError(Exception e) {
        Log.e("Parse Exception " + e + "");
        Toast.makeText(this.getContext(), "ERROR: " + e.toString(), Toast.LENGTH_LONG).show();
    }
}
