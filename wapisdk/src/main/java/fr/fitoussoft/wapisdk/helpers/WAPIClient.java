package fr.fitoussoft.wapisdk.helpers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import fr.fitoussoft.wapisdk.R;
import fr.fitoussoft.wapisdk.activities.AuthActivity;
import fr.fitoussoft.wapisdk.activities.IWapiActivity;
import fr.fitoussoft.wapisdk.models.Account;
import fr.fitoussoft.wapisdk.models.Reflection;


/**
 * Created by emmanuel.fitoussi on 07/10/2014.
 */
public class WAPIClient {
    private final static String PARAM_CLIENT_ID = "client_id";
    private final static String PARAM_CLIENT_SECRET = "client_secret";
    private final static String PARAM_CODE = "code";
    private final static String PARAM_GRANT_TYPE = "grant_type";
    private final static String PARAM_REDIRECT_URI = "redirect_uri";
    private final static String PARAM_AUTHORIZATION_CODE = "authorization_code";
    private final static String PARAM_REFRESH_TOKEN = "refresh_token";
    private final static String JSON_FIELD_ACCESS_TOKEN = "access_token";
    private final static String JSON_FIELD_REFRESH_TOKEN = "refresh_token";
    private final static String JSON_FIELD_EXPIRES_IN = "expires_in";
    private final static String JSON_FIELD_REFLECTIONS = "reflections";
    public static boolean DEBUG = true;
    public int nextSkip = 0;
    private Config config;

    private HttpClient httpClient;
    private Context _context;
    private WAPIToken _token;

    public WAPIClient(Context context, SharedPreferences prefs) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        httpClient = WAPIClient.getNewHttpClient();
        _context = context;
        Resources res = _context.getResources();

        config = new Config();

        config.clientId = res.getString(R.string.client_id);
        config.clientSecret = res.getString(R.string.client_secret);
        config.wapiAuthorise = res.getString(R.string.wapi_authorise);
        config.wapiToken = res.getString(R.string.wapi_token);
        config.wapiGetBusinessAcountsMy = res.getString(R.string.wapi_GetBusinessAccountsMy);
        config.wapiSearchReflections = res.getString(R.string.wapi_SearchReflections);
        config.wapiLoadPicture = res.getString(R.string.wapi_LoadPicture);

        if (DEBUG) {
            config.clientId = res.getString(R.string.client_id_beta);
            config.clientSecret = res.getString(R.string.client_secret_beta);
            config.wapiAuthorise = res.getString(R.string.wapi_authorise_beta);
            config.wapiToken = res.getString(R.string.wapi_token_beta);
            config.wapiGetBusinessAcountsMy = res.getString(R.string.wapi_GetBusinessAccountsMy_beta);
            config.wapiSearchReflections = res.getString(R.string.wapi_SearchReflections_beta);
            config.wapiLoadPicture = res.getString(R.string.wapi_LoadPicture_beta);
        }

        // restore stored tokens
        _token = new WAPIToken(prefs, DEBUG);
    }

    private static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new PermisiveSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);


            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    private static void navigateToAuth(Activity activity) {
        Intent myIntent = new Intent(activity, AuthActivity.class);
        activity.startActivityForResult(myIntent, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void disconnect(final Activity activity) {
        Log.d("disconnect.");
        _token.resetTokens();
        Log.d("SDK: " + Build.VERSION.SDK_INT + ", JELLY BEAN: " + Build.VERSION_CODES.KITKAT);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.flush();
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {

                @Override
                public void onReceiveValue(Boolean aBoolean) {
                    WAPIClient.navigateToAuth(activity);
                }
            });
        } else {
            CookieSyncManager.createInstance(_context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            WAPIClient.navigateToAuth(activity);
        }
    }

    public Config getConfig() {
        return config;
    }

    public boolean hasToAuthenticate() {
        return !_token.hasRefreshToken();
    }

    public boolean hasToRefreshAccessToken() {
        return !_token.hasAccessToken() || _token.hasExpired();
    }

    public void verifyAuthentication(final Activity origin) {
        if (hasToAuthenticate()) {
            navigateToAuth(origin);
            return;
        }

        final IWapiActivity originActivity = (IWapiActivity) origin;

        if (hasToRefreshAccessToken()) {
            RequestRefreshAccessTokenAsyncTask task = new RequestRefreshAccessTokenAsyncTask() {
                @Override
                protected void onPostExecute(Boolean result) {
                    originActivity.onAuthenticated(WAPIClient.this);
                }
            };
            task.execute();
        }

        originActivity.onAuthenticated(this);
    }

    public String get(String url, boolean withAccessToken) {
        Log.d("GET " + url + ", with accessToken: " + withAccessToken);
        HttpGet get = new HttpGet(url);
        String responseText = null;
        try {

            if (withAccessToken) {
                addAccessTokenToHeader(get);
            }

            HttpResponse response = httpClient.execute(get);
            responseText = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            logError(e);
        }

        Log.d(responseText);
        return responseText;
    }

    private void logError(Exception e) {
        Log.e("Parse Exception " + e + "");
        Toast.makeText(this._context, "ERROR: " + e.toString(), Toast.LENGTH_LONG).show();
    }

    // TODO refactor with get method
    public byte[] getByteArray(String url, boolean withAccessToken) {
        Log.d("GET " + url + ", with accessToken: " + withAccessToken);
        HttpGet get = new HttpGet(url);
        byte[] responseArray = null;
        try {

            if (withAccessToken) {
                addAccessTokenToHeader(get);
            }

            HttpResponse response = httpClient.execute(get);
            responseArray = EntityUtils.toByteArray(response.getEntity());
        } catch (Exception e) {
            logError(e);
        }

        return responseArray;
    }

    private void addAccessTokenToHeader(HttpRequestBase httpMethod) {
        httpMethod.addHeader("Authorization", "Bearer " + _token.getAccessToken());
    }

    public String post(String url, List<NameValuePair> pairs, boolean withAccessToken) {
        Log.d("POST " + url + ", with accessToken: " + withAccessToken);
        HttpPost post = new HttpPost(url);
        String responseText = null;
        try {
            if (pairs != null) {
                post.setEntity(new UrlEncodedFormEntity(pairs));
            }

            if (withAccessToken) {
                addAccessTokenToHeader(post);
            }

            HttpResponse response = httpClient.execute(post);
            responseText = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            logError(e);
        }

        Log.d(responseText);
        return responseText;
    }

    public String post(String url, boolean withAccessToken) {
        return this.post(url, null, withAccessToken);
    }

    public boolean requestAccessToken(String code) {
        boolean result = false;
        Resources res = _context.getResources();
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair(PARAM_CLIENT_ID, config.clientId));
        pairs.add(new BasicNameValuePair(PARAM_CLIENT_SECRET, config.clientSecret));
        pairs.add(new BasicNameValuePair(PARAM_CODE, code));
        pairs.add(new BasicNameValuePair(PARAM_GRANT_TYPE, PARAM_AUTHORIZATION_CODE));
        pairs.add(new BasicNameValuePair(PARAM_REDIRECT_URI, res.getString(R.string.redirect_uri)));
        try {
            String responseText = this.post(config.wapiToken, pairs, false);
            JSONObject json = new JSONObject(responseText);
            _token.setTokens(json.getString(JSON_FIELD_ACCESS_TOKEN),
                    json.getString(JSON_FIELD_REFRESH_TOKEN),
                    json.getInt(JSON_FIELD_EXPIRES_IN));
            result = true;
        } catch (Exception e) {
            logError(e);
        }

        return result;
    }

    public boolean requestRefreshAccessToken() {
        boolean result = false;
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair(PARAM_CLIENT_ID, config.clientId));
        pairs.add(new BasicNameValuePair(PARAM_CLIENT_SECRET, config.clientSecret));
        pairs.add(new BasicNameValuePair(PARAM_REFRESH_TOKEN, _token.getRefreshToken()));
        pairs.add(new BasicNameValuePair(PARAM_GRANT_TYPE, PARAM_REFRESH_TOKEN));

        try {
            String responseText = this.post(config.wapiToken, pairs, false);
            JSONObject json = new JSONObject(responseText);
            _token.setTokens(json.getString(JSON_FIELD_ACCESS_TOKEN),
                    json.getString(JSON_FIELD_REFRESH_TOKEN),
                    json.getInt(JSON_FIELD_EXPIRES_IN));
            result = true;
        } catch (ParseException e) {
            logError(e);
        } catch (Exception e) {
            Log.e("Unknown Exception " + e + "");
            Toast.makeText(this._context, "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    public List<Account> requestBusinessAccounts() {
        List<Account> accounts = new ArrayList<Account>();
        try {
            String responseText = this.get(config.wapiGetBusinessAcountsMy, true);
            JSONArray json = new JSONArray(responseText);
            Account account;
            Resources res = _context.getResources();
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonO = (JSONObject) json.get(i);
                account = new Account(jsonO);
                byte[] pictureBytes = requestPicture(account.getPictureId(), res.getString(R.string.icon_size));
                account.setPictureBytes(pictureBytes);

                Bitmap picture = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);
                account.setPicture(picture);

                accounts.add(account);

            }
            Log.d("json=" + json);
        } catch (Exception e) {
            logError(e);
        }

        return accounts;
    }

    public List<Reflection> requestReflections(String wac, int skip, int take) {
        List<Reflection> reflections = new ArrayList<Reflection>();
        try {
            String url = String.format(config.wapiSearchReflections, wac, skip, take);
            String responseText = this.get(url, true);
            JSONObject jsonContainer = new JSONObject(responseText);
            JSONArray json = jsonContainer.getJSONArray(JSON_FIELD_REFLECTIONS);
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonO = (JSONObject) json.get(i);
                reflections.add(new Reflection(jsonO));
            }
            Log.d("json=" + json);
        } catch (Exception e) {
            logError(e);
        }

        return reflections;
    }

    public byte[] requestPicture(String id, String size) {
        byte[] pictureBytes = null;
        try {
            String url = String.format(config.wapiLoadPicture, id, size);
            Log.d("url: " + url);
            pictureBytes = this.getByteArray(url, true);
            Log.d("pictureBytes=" + pictureBytes.length);
        } catch (Exception e) {
            logError(e);
        }

        return pictureBytes;
    }

    public List<Reflection> requestNextReflections(String wac) {
        int pageSize = 20;
        int newSkip = nextSkip;
        nextSkip += pageSize;
        return requestReflections(wac, newSkip, pageSize);
    }

    public class Config {
        public String clientId;
        public String clientSecret;
        public String wapiAuthorise;
        public String wapiToken;
        public String wapiGetBusinessAcountsMy;
        public String wapiSearchReflections;
        public String wapiLoadPicture;
    }

    public abstract class RequestRefreshAccessTokenAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return WAPIClient.this.requestRefreshAccessToken();
        }

        @Override
        protected abstract void onPostExecute(Boolean result);
    }



    public abstract class RequestNextReflectionsAsyncTask extends AsyncTask<String, Integer, List<Reflection>> {
        @Override
        protected List<Reflection> doInBackground(String... strings) {
            String wac = strings[0];
            return WAPIClient.this.requestNextReflections(wac);
        }

        @Override
        protected abstract void onPostExecute(List<Reflection> reflections);
    }

    public abstract class RequestRequestBusinessAccountsAsyncTask extends AsyncTask<Void, Integer, List<Account>> {
        @Override
        protected List<Account> doInBackground(Void... voids) {
            return WAPIClient.this.requestBusinessAccounts();
        }

        @Override
        protected abstract void onPostExecute(List<Account> accounts);
    }


}
