package fr.fitoussoft.wapisdk.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;

import fr.fitoussoft.wapisdk.R;
import fr.fitoussoft.wapisdk.helpers.Log;
import fr.fitoussoft.wapisdk.helpers.WAPIClient;
import fr.fitoussoft.wapisdk.services.WAPIService;

public class AuthActivity extends Activity {

    private WebView loginWebView;
    private WebViewClient loginWebViewClient;
    private AsyncTask<String, Integer, Boolean> requestAccessTask;


    private AsyncTask<String, Integer, Boolean> createRequestAsyncTask() {
        return new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                WAPIClient client = WAPIService.getClient();
                return client.requestAccess(strings[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    AuthActivity.this.finish();
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

                Log.d("code=" + code);
                return code;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("page loaded with url: " + url);
                String code = this.extractCodeFromURL(url);
                if (code == null) {
                    Log.d("code not found.");
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
        WAPIClient client = WAPIService.getClient();
        String url = String.format(client.getConfig().wapiAuthorise,
                client.getConfig().clientId,
                res.getString(R.string.redirect_uri));
        Log.d("url_authorise=" + url);
        loginWebView.loadUrl(url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Auth onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onResume() {
        Log.d("Auth onResume.");


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


        super.onResume();
    }
}
