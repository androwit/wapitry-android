package fr.fitoussoft.wapisdk.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;

import fr.fitoussoft.wapisdk.IWapiApplication;
import fr.fitoussoft.wapisdk.R;
import fr.fitoussoft.wapisdk.helpers.Log;
import fr.fitoussoft.wapisdk.helpers.WAPIClient;

public class AuthActivity extends Activity implements IWapiActivity {

    private WebView loginWebView;
    private WebViewClient loginWebViewClient;
    private WAPIClient.RequestAccessTokenAsyncTask requestAccessTask;

    private WAPIClient getWapiClient() {
        return ((IWapiApplication) this.getApplication()).getWapiClient();
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
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String code = this.extractCodeFromURL(url);
                if (code != null) {
                    // hides WebView.
                    view.setVisibility(View.INVISIBLE);

                    // use code in POST request to get token.
                    requestAccessTask.getParams().put(WAPIClient.PARAM_CODE, code);
                    requestAccessTask.execute();
                    return;
                }

                view.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }
        };
    }

    private void authenticate(WAPIClient client) {
        Resources res = getResources();
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
        super.onResume();

        WAPIClient client = getWapiClient();

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
            requestAccessTask = client.new RequestAccessTokenAsyncTask() {

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result) {
                        AuthActivity.this.finish();
                    }
                }
            };
        }

        authenticate(client);
    }

    @Override
    public void onAuthenticated(WAPIClient wapiClient) {
        // nothing to do.
    }
}
