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

import java.util.LinkedList;
import java.util.Queue;

import fr.fitoussoft.wapisdk.activities.AuthActivity;
import fr.fitoussoft.wapisdk.tasks.RequestAsyncTaskBase;
import fr.fitoussoft.wapisdk.tasks.RequestRefreshAccessTokenAsyncTask;


/**
 * Created by emmanuel.fitoussi on 07/10/2014.
 */
public class WapiClient {
    public static boolean DEBUG = true;
    private Configuration config;
    private boolean isAuthenticating = false;
    private boolean isRefreshingToken = false;
    private Context context;
    private WapiToken token;
    private Queue<RequestAsyncTaskBase> taskQueue = new LinkedList<RequestAsyncTaskBase>();

    public WapiClient(Context context, SharedPreferences prefs) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.context = context;
        Resources res = this.context.getResources();

        config = new Configuration(res, DEBUG);

        // restore stored tokens
        token = new WapiToken(prefs, DEBUG);
    }

    public boolean isRefreshingToken() {
        return isRefreshingToken;
    }

    public void setRefreshingToken(boolean isRefreshingToken) {
        this.isRefreshingToken = isRefreshingToken;
    }

    public boolean isAuthenticating() {
        return isAuthenticating;
    }

    public void setAuthenticating(boolean isAuthenticating) {
        this.isAuthenticating = isAuthenticating;
    }

    private void navigateToAuth(Activity activity) {
        isAuthenticating = true;
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
                    navigateToAuth(activity);
                }
            });
        } else {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            navigateToAuth(activity);
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

    public void verifyAuthentication(RequestAsyncTaskBase task) {
        if (isRefreshingToken) {
            taskQueue.add(task);
            return;
        }

        if (hasToAuthenticate()) {
            navigateToAuth(task.getOrigin());
            task.cancel(true);
            return;
        }

        if (hasToRefreshAccessToken()) {
            setRefreshingToken(true);
            RequestRefreshAccessTokenAsyncTask refreshTask = new RequestRefreshAccessTokenAsyncTask(task.getOrigin()) {
                @Override
                protected void onPostExecute(Boolean result) {
                    setRefreshingToken(false);
                    RequestAsyncTaskBase taskInQueue;
                    while ((taskInQueue = taskQueue.poll()) != null) {
                        taskInQueue.onAuthenticated();
                    }
                }
            };
            refreshTask.execute();
            taskQueue.add(task);
            return;
        }

        task.onAuthenticated();
    }


    public void logError(Exception e) {
        Log.e("Parse Exception " + e + "");
        Toast.makeText(this.getContext(), "ERROR: " + e.toString(), Toast.LENGTH_LONG).show();
    }
}
