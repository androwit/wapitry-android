package fr.fitoussoft.wapitry.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ParseException;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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

import java.io.IOException;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.fitoussoft.wapitry.R;
import fr.fitoussoft.wapitry.activities.MainActivity;
import fr.fitoussoft.wapitry.models.Account;

/**
 * Created by emmanuel.fitoussi on 07/10/2014.
 */
public class WAPIClient {

    private HttpClient _client;
    private String _refreshToken = "";
    private String _accessToken = "";
    private Calendar _expireDate;
    private MainActivity _main;
    private SharedPreferences _prefs;

    public WAPIClient(MainActivity main) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        _client = WAPIClient.getNewHttpClient();
        _main = main;


        // restore stored tokens
        _prefs = _main.getPreferences(Context.MODE_PRIVATE);
        this.loadTokens();
    }

    public static HttpClient getNewHttpClient() {
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

    public HttpClient getClient() {
        return _client;
    }

    public boolean hasAccessToken() {
        return _accessToken != null && !_accessToken.isEmpty();
    }

    public boolean hasRefreshToken() {
        return _refreshToken != null && !_refreshToken.isEmpty();
    }

    public boolean hasExpired() {
        return _expireDate == null || _expireDate.before(Calendar.getInstance());
    }

    public boolean haveToRefreshAccessToken() {
        return this.hasExpired() && this.hasRefreshToken();
    }

    public boolean haveToGetRefreshToken() {
        return !this.hasRefreshToken();
    }

    public String get(String url, boolean withAccessToken) {
        Log.d("[TRY]", "GET " + url + ", with accessToken: " + withAccessToken);
        HttpGet get = new HttpGet(url);
        String responseText = null;
        try {

            if (withAccessToken) {
                get.addHeader("Authorization", "Bearer " + _accessToken);
            }

            HttpResponse response = _client.execute(get);
            responseText = EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            Log.e("[TRY]", "Parse Exception " + e + "");
        } catch (IOException e) {
            Log.e("[TRY]", "IO Exception " + e + "");
        } catch (Exception e) {
            Log.e("[TRY]", "Unknown Exception " + e + "");
        }

        Log.d("[TRY]", responseText);
        return responseText;
    }

    public String post(String url, List<NameValuePair> pairs, boolean withAccessToken) {
        Log.d("[TRY]", "POST " + url + ", with accessToken: " + withAccessToken);
        HttpPost post = new HttpPost(url);
        String responseText = null;
        try {
            if (pairs != null) {
                post.setEntity(new UrlEncodedFormEntity(pairs));
            }

            if (withAccessToken) {
                post.addHeader("Authorization", "Bearer " + _accessToken);
            }

            HttpResponse response = _client.execute(post);
            responseText = EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            Log.e("[TRY]", "Parse Exception " + e + "");
        } catch (IOException e) {
            Log.e("[TRY]", "IO Exception " + e + "");
        } catch (Exception e) {
            Log.e("[TRY]", "Unknown Exception " + e + "");
        }

        Log.d("[TRY]", responseText);
        return responseText;
    }

    public String post(String url, boolean withAccessToken) {
        return this.post(url, null, withAccessToken);
    }

    public void saveTokens() {
        SharedPreferences.Editor editor = _prefs.edit();
        editor.putString("accessToken", _accessToken);
        editor.putString("refreshToken", _refreshToken);
        editor.putLong("expireTime", _expireDate.getTimeInMillis());
        editor.commit();
    }

    public void loadTokens() {
        _refreshToken = _prefs.getString("refreshToken", "");
        _accessToken = _prefs.getString("accessToken", "");
        long expireTime = _prefs.getLong("expireTime", 0);
        _expireDate = Calendar.getInstance();
        _expireDate.setTimeInMillis(expireTime);
    }

    public void resetTokens() {
        SharedPreferences.Editor editor = _prefs.edit();
        editor.remove("accessToken");
        editor.remove("refreshToken");
        editor.remove("expireTime");
        editor.commit();
    }

    public boolean requestAccess(String code) {
        boolean result = false;
        Resources res = _main.getResources();
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("client_id", res.getString(R.string.client_id)));
        pairs.add(new BasicNameValuePair("client_secret", res.getString(R.string.client_secret)));
        pairs.add(new BasicNameValuePair("code", code));
        pairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
        pairs.add(new BasicNameValuePair("redirect_uri", res.getString(R.string.redirect_uri)));

        try {
            String responseText = this.post(res.getString(R.string.wapi_token), pairs, false);
            JSONObject json = new JSONObject(responseText);
            _accessToken = json.getString("access_token");
            _refreshToken = json.getString("refresh_token");
            _expireDate = Calendar.getInstance();
            _expireDate.add(Calendar.SECOND, json.getInt("expires_in"));

            this.saveTokens();

            Log.d("[TRY]", "accessToken=" + _accessToken);
            Log.d("[TRY]", "refreshToken=" + _refreshToken);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Log.d("[TRY]", "expires=" + sdf.format(_expireDate.getTime()));
            result = true;
        } catch (ParseException e) {
            Log.e("[TRY]", "Parse Exception " + e + "");
        } catch (Exception e) {
            Log.e("[TRY]", "Unknown Exception " + e + "");
        }

        return result;
    }

    public boolean refreshAccess() {
        boolean result = false;
        Resources res = _main.getResources();
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("client_id", res.getString(R.string.client_id)));
        pairs.add(new BasicNameValuePair("client_secret", res.getString(R.string.client_secret)));
        pairs.add(new BasicNameValuePair("refresh_token", _refreshToken));
        pairs.add(new BasicNameValuePair("grant_type", "refresh_token"));

        try {
            String responseText = this.post(res.getString(R.string.wapi_token), pairs, false);
            JSONObject json = new JSONObject(responseText);
            _accessToken = json.getString("access_token");
            _refreshToken = json.getString("refresh_token");
            _expireDate = Calendar.getInstance();
            _expireDate.add(Calendar.SECOND, json.getInt("expires_in"));

            this.saveTokens();

            Log.d("[TRY]", "accessToken=" + _accessToken);
            Log.d("[TRY]", "refreshToken=" + _refreshToken);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Log.d("[TRY]", "expires=" + sdf.format(_expireDate.getTime()));
            result = true;
        } catch (ParseException e) {
            Log.e("[TRY]", "Parse Exception " + e + "");
        } catch (Exception e) {
            Log.e("[TRY]", "Unknown Exception " + e + "");
        }

        return result;
    }

    public List<Account> requestBusinessAccounts() {
        List<Account> accounts = new ArrayList<Account>();
        try {
            Resources res = _main.getResources();
            String responseText = this.get(res.getString(R.string.wapi_GetBusinessAccountsMy), true);
            JSONArray json = new JSONArray(responseText);
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonO = (JSONObject) json.get(i);
                accounts.add(new Account(jsonO));
            }
            Log.d("[TRY]", "json=" + json);
        } catch (ParseException e) {
            Log.e("[TRY]", "Parse Exception " + e + "");
        } catch (Exception e) {
            Log.e("[TRY]", "Unknown Exception " + e + "");
        }

        return accounts;
    }
}
