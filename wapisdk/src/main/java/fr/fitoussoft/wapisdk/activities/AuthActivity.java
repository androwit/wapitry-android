package fr.fitoussoft.wapisdk.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;

import fr.fitoussoft.wapisdk.helpers.WAPIClient;
import fr.fitoussoft.wapisdk.R;

public class AuthActivity extends Activity {


    private static WAPIClient _client;
    private WebView loginWebView;
    private WebViewClient loginWebViewClient;
    private AsyncTask<String, Integer, Boolean> requestAccessTask;

    public static WAPIClient getClient() {
        return _client;
    }

    public static WAPIClient setupWAPIClient(Activity mainActivity) {
        if (_client == null) {
            // setups WAPIClient.
            _client = new WAPIClient(mainActivity);
        }

        return _client;
    }

    public void navigateToMain() {
        Intent myIntent = new Intent(this, getClient().getMainActivity().getClass());
        this.startActivity(myIntent);
    }

    private AsyncTask<String, Integer, Boolean> createRequestAsyncTask() {
        return new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                WAPIClient client = getClient();
                return client.requestAccess(strings[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    navigateToMain();
                }
            }
        };
    }

    private WebViewClient createWebViewClient() {
        return new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            private String extractCodeFromURL(String url) {
                String fieldPattern = "code=";
                int index = url.indexOf(fieldPattern);
                String code = null;
                if (index != -1) {
                    // extract code.
                    code = url.substring(index + fieldPattern.length());

                    try {
                        code = java.net.URLDecoder.decode(code, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        code = null;
                    }
                }

                Log.d("[TRY]", "code=" + code);
                return code;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("[TRY]", "page loaded with url: " + url);
                String code = this.extractCodeFromURL(url);
                if (code == null) {
                    Log.d("[TRY]", "code not found.");
                    view.setVisibility(View.VISIBLE);
                    super.onPageFinished(view, url);
                    return;
                }

                // hides WebView.
                view.setVisibility(View.INVISIBLE);

                // use code in POST request to get token.
                requestAccessTask.execute(code);
            }
        };
    }

    private void authenticate() {
        Resources res = getResources();
        WAPIClient client = getClient();
        String url = String.format(client.getConfig().wapiAuthorise,
                client.getConfig().clientId,
                res.getString(R.string.redirect_uri));
        Log.d("[TRY]", "url_authorise=" + url);
        loginWebView.loadUrl(url);
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("[TRY]", "Auth onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // setup.
        if (loginWebView == null) {
            loginWebView = (WebView) findViewById(R.id.webView);
            WebSettings ws = loginWebView.getSettings();
            if (loginWebViewClient == null) {
                loginWebViewClient = createWebViewClient();
                loginWebView.setWebViewClient(loginWebViewClient);
                loginWebView.setVisibility(View.INVISIBLE);
            }

            ws.setSaveFormData(false);
            loginWebView.getSettings().setJavaScriptEnabled(true);
        }

        if (requestAccessTask == null) {
            requestAccessTask = createRequestAsyncTask();
        }

        authenticate();
    }


    @Override
    protected void onResume() {
        Log.d("[TRY]", "Auth onResume.");
        super.onResume();
    }
}