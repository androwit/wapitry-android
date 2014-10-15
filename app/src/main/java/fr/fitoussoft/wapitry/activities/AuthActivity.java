package fr.fitoussoft.wapitry.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;

import fr.fitoussoft.wapitry.R;
import fr.fitoussoft.wapitry.helpers.WAPIClient;

public class AuthActivity extends Activity {


    private WebView loginWebView;
    private WebViewClient loginWebViewClient;

    public void navigateToAccounts() {
        Intent myIntent = new Intent(AuthActivity.this, AccountsActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        AuthActivity.this.startActivity(myIntent);
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
                    super.onPageFinished(view, url);
                    return;
                }

                // hides WebView.
                view.setVisibility(View.INVISIBLE);

                // use code in POST request to get token.
                WAPIClient client = MainActivity.getClient();
                boolean result = client.requestAccess(code);
                if (result) {
                    navigateToAccounts();
                }
            }
        };
    }

    private void authenticate() {
        Resources res = getResources();
        String url = String.format(res.getString(R.string.wapi_authorise),
                res.getString(R.string.client_id),
                res.getString(R.string.redirect_uri));
        Log.d("[TRY]", "url_authorise=" + url);
        loginWebView.setVisibility(View.VISIBLE);
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
            }

            ws.setSaveFormData(false);
            loginWebView.getSettings().setJavaScriptEnabled(true);
        }

        authenticate();
    }


    @Override
    protected void onResume() {
        Log.d("[TRY]", "Auth onResume.");
        super.onResume();
    }

}
