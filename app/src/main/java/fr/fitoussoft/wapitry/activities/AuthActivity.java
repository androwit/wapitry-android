package fr.fitoussoft.wapitry.activities;

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


    public void navigateToAccounts() {
        Intent myIntent = new Intent(AuthActivity.this, AccountsActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        AuthActivity.this.startActivity(myIntent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Log.d("[TRY]", "oncreate.");
        Resources res = getResources();

        // get WAPI client.
        WAPIClient client = MainActivity.getClient();

        WebView loginWebView = (WebView) findViewById(R.id.webView);
        loginWebView.setVisibility(View.VISIBLE);
        WebSettings ws = loginWebView.getSettings();
        ws.setSaveFormData(false);
        loginWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("[TRY]", "page loaded with url: " + url);
                String fieldPattern = "code=";
                int index = url.indexOf(fieldPattern);
                if (index == -1) {
                    super.onPageFinished(view, url);
                } else {
                    view.setVisibility(View.INVISIBLE);
                    // extract code.
                    String code = url.substring(index + fieldPattern.length());

                    try {
                        code = java.net.URLDecoder.decode(code, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Log.d("[TRY]", "code=" + code);

                    // use code in POST request to get token.
                    WAPIClient client = MainActivity.getClient();
                    boolean result = client.requestAccess(code);

                    if (result) {
                        navigateToAccounts();
                    }
                }
            }
        });
        loginWebView.getSettings().setJavaScriptEnabled(true);
        String url = String.format(res.getString(R.string.wapi_authorise),
                res.getString(R.string.client_id),
                res.getString(R.string.redirect_uri));
        Log.d("[TRY]", "url_authorise=" + url);
        loginWebView.loadUrl(url);
    }
}
