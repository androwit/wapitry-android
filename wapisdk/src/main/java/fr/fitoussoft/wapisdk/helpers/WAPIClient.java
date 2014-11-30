package fr.fitoussoft.wapisdk.helpers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
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
        config.redirectUrl = res.getString(R.string.redirect_uri);

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

    private void logError(Exception e) {
        Log.e("Parse Exception " + e + "");
        Toast.makeText(this._context, "ERROR: " + e.toString(), Toast.LENGTH_LONG).show();
    }

    private void addAccessTokenToHeader(HttpRequestBase httpMethod) {
        httpMethod.addHeader("Authorization", "Bearer " + _token.getAccessToken());
    }

    /**
     * Created by emmanuel.fitoussi on 29/11/2014.
     */
    public static interface IRequestBase<T> {
        T execute();
    }

    public abstract class RequestBase<T extends HttpRequestBase, U> implements IRequestBase<U> {
        protected boolean isWithAccessToken = false;
        protected String url;
        protected T httpMethod;
        protected U response;
        protected List<NameValuePair> params;

        public RequestBase(String url, boolean isWithAccessToken, Class<T> httpMethodClass) {
            this(url, null, isWithAccessToken, httpMethodClass);
        }

        public RequestBase(String url, List<NameValuePair> params, boolean isWithAccessToken, Class<T> httpMethodClass) {
            try {
                this.url = url;
                this.isWithAccessToken = isWithAccessToken;
                this.httpMethod = httpMethodClass.newInstance();
                this.params = params;

                if (this.isWithAccessToken) {
                    addAccessTokenToHeader(httpMethod);
                }

                if (params != null) {
                    String key;
                    List<NameValuePair> newParams = new ArrayList<NameValuePair>(params);

                    // replace url keys with params ad remove these params.
                    for (NameValuePair param : params) {
                        key = "\\[" + param.getName() + "\\]";
                        if (url.matches(".*" + key + ".*")) {
                            url = url.replaceAll(key, param.getValue());
                            newParams.remove(param);
                        }
                    }

                    if (httpMethod instanceof HttpEntityEnclosingRequestBase) {
                        ((HttpEntityEnclosingRequestBase) httpMethod).setEntity(new UrlEncodedFormEntity(newParams));
                    } else {
                        url += "?" + URLEncodedUtils.format(newParams, "utf-8");
                    }
                }

                this.httpMethod.setURI(URI.create(url));
            } catch (Exception e) {
                logError(e);
            }
        }

        public List<NameValuePair> getParams() {
            return params;
        }

        public String getUrl() {
            return url;
        }

        public U getResponse() {
            return response;
        }

        public T getHttpMethod() {
            return httpMethod;
        }

        public boolean isWithAccessToken() {
            return isWithAccessToken;
        }

        @Override
        public U execute() {
            try {
                response = castResponse(httpClient.execute(httpMethod));
            } catch (Exception e) {
                logError(e);
            }

            return response;
        }

        protected abstract U castResponse(HttpResponse httpResponse);
    }

    public class RequestString<T extends HttpRequestBase> extends RequestBase<T, String> {

        public RequestString(String url, boolean isWithAccessToken, Class<T> httpMethodClass) {
            super(url, null, isWithAccessToken, httpMethodClass);
        }

        public RequestString(String url, List<NameValuePair> params, boolean isWithAccessToken, Class<T> httpMethodClass) {
            super(url, params, isWithAccessToken, httpMethodClass);
        }

        @Override
        protected String castResponse(HttpResponse httpResponse) {
            try {
                return EntityUtils.toString(httpResponse.getEntity());
            } catch (IOException e) {
                logError(e);
            }

            return null;
        }
    }

    public class RequestByteArray<T extends HttpRequestBase> extends RequestBase<T, byte[]> {

        public RequestByteArray(String url, List<NameValuePair> params, boolean isWithAccessToken, Class<T> httpMethodClass) {
            super(url, params, isWithAccessToken, httpMethodClass);
        }

        @Override
        protected byte[] castResponse(HttpResponse httpResponse) {
            try {
                return EntityUtils.toByteArray(httpResponse.getEntity());
            } catch (IOException e) {
                logError(e);
                return null;
            }
        }
    }

    public class Config {
        public String clientId;
        public String clientSecret;
        public String wapiAuthorise;
        public String wapiToken;
        public String wapiGetBusinessAcountsMy;
        public String wapiSearchReflections;
        public String wapiLoadPicture;
        public String redirectUrl;
    }

    public abstract class RequestAsyncTaskBase<V, R> extends AsyncTask<Void, Integer, V> {

        protected IRequestBase<R> request;
        protected Parameters params;

        public RequestAsyncTaskBase() {
            this.params = createParams();
        }

        public Parameters getParams() {
            return params;
        }

        protected abstract IRequestBase<R> createRequest(List<NameValuePair> params);

        protected abstract Parameters createParams();

        @Override
        protected V doInBackground(Void... voids) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (String key : this.params.keySet()) {
                params.add(new BasicNameValuePair(key, this.params.get(key)));
            }

            this.request = createRequest(params);
            R responseResult = request.execute();
            return onResponseDone(responseResult);
        }

        protected abstract V onResponseDone(R responseResult);

        @Override
        protected abstract void onPostExecute(V result);

        public class Parameters extends HashMap<String, String> {
        }
    }

    public abstract class RequestAccessTokenAsyncTask extends RequestAsyncTaskBase<Boolean, String> {

        public final static String PARAM_CODE = "code";
        protected final static String PARAM_CLIENT_ID = "client_id";
        protected final static String PARAM_CLIENT_SECRET = "client_secret";
        protected final static String PARAM_GRANT_TYPE = "grant_type";
        protected final static String PARAM_REFRESH_TOKEN = "refresh_token";
        private final static String PARAM_REDIRECT_URI = "redirect_uri";
        private final static String PARAM_AUTHORIZATION_CODE = "authorization_code";
        private final static String JSON_FIELD_ACCESS_TOKEN = "access_token";
        private final static String JSON_FIELD_REFRESH_TOKEN = "refresh_token";
        private final static String JSON_FIELD_EXPIRES_IN = "expires_in";

        @Override
        protected Parameters createParams() {
            Parameters params = new Parameters();
            params.put(PARAM_CODE, "");
            return params;
        }

        @Override
        protected RequestString<HttpPost> createRequest(List<NameValuePair> params) {
            params.add(new BasicNameValuePair(PARAM_CLIENT_ID, config.clientId));
            params.add(new BasicNameValuePair(PARAM_CLIENT_SECRET, config.clientSecret));
            params.add(new BasicNameValuePair(PARAM_GRANT_TYPE, PARAM_AUTHORIZATION_CODE));
            params.add(new BasicNameValuePair(PARAM_REDIRECT_URI, config.redirectUrl));
            return new RequestString<HttpPost>(config.wapiToken, params, false, HttpPost.class);
        }

        @Override
        protected Boolean onResponseDone(String responseResult) {
            boolean result = false;
            try {
                JSONObject json = new JSONObject(responseResult);
                _token.setTokens(json.getString(JSON_FIELD_ACCESS_TOKEN),
                        json.getString(JSON_FIELD_REFRESH_TOKEN),
                        json.getInt(JSON_FIELD_EXPIRES_IN));
                result = true;
            } catch (JSONException e) {
                logError(e);
            }

            return result;
        }

        @Override
        protected abstract void onPostExecute(Boolean result);
    }

    public abstract class RequestRefreshAccessTokenAsyncTask extends RequestAccessTokenAsyncTask {

        @Override
        protected Parameters createParams() {
            Parameters params = new Parameters();
            params.put(PARAM_CLIENT_ID, config.clientId);
            params.put(PARAM_CLIENT_SECRET, config.clientSecret);
            params.put(PARAM_REFRESH_TOKEN, _token.getRefreshToken());
            params.put(PARAM_GRANT_TYPE, PARAM_REFRESH_TOKEN);
            return params;
        }

        @Override
        protected RequestString<HttpPost> createRequest(List<NameValuePair> params) {
            return new RequestString<HttpPost>(config.wapiToken, params, false, HttpPost.class);
        }

        @Override
        protected abstract void onPostExecute(Boolean result);
    }

    public abstract class RequestNextReflectionsAsyncTask extends RequestAsyncTaskBase<List<Reflection>, String> {

        public final static String PARAM_WAC = "WAC";
        public final static int PAGE_SIZE = 20;
        protected final static String PARAM_SKIP = "skip";
        protected final static String PARAM_TAKE = "take";
        private final static String JSON_FIELD_REFLECTIONS = "reflections";


        @Override
        protected Parameters createParams() {
            Parameters params = new Parameters();
            params.put(PARAM_WAC, "");
            return params;
        }

        @Override
        protected IRequestBase<String> createRequest(List<NameValuePair> params) {
            int newSkip = nextSkip;
            nextSkip += PAGE_SIZE;
            params.add(new BasicNameValuePair(PARAM_SKIP, String.valueOf(newSkip)));
            params.add(new BasicNameValuePair(PARAM_TAKE, String.valueOf(PAGE_SIZE)));
            return new RequestString<HttpGet>(config.wapiSearchReflections, params, true, HttpGet.class);
        }

        @Override
        protected List<Reflection> onResponseDone(String response) {
            List<Reflection> reflections = new ArrayList<Reflection>();
            try {
                JSONObject jsonContainer = new JSONObject(response);
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


        @Override
        protected abstract void onPostExecute(List<Reflection> reflections);
    }

    public abstract class RequestPictureAsyncTask extends RequestAsyncTaskBase<byte[], byte[]> {

        public static final String PARAM_ID = "id";
        public static final String PARAM_SIZE = "size";

        @Override
        protected Parameters createParams() {
            Parameters params = new Parameters();
            params.put(PARAM_ID, "");
            params.put(PARAM_SIZE, "");
            return params;
        }

        @Override
        protected IRequestBase<byte[]> createRequest(List<NameValuePair> params) {
            return new RequestByteArray<HttpGet>(config.wapiLoadPicture, params, true, HttpGet.class);
        }

        @Override
        protected byte[] onResponseDone(byte[] pictureBytes) {
            Log.d("pictureBytes=" + pictureBytes.length);
            return pictureBytes;
        }

        @Override
        protected abstract void onPostExecute(byte[] result);
    }

    public abstract class RequestBusinessAccountsAsyncTask extends RequestAsyncTaskBase<List<Account>, String> {

        @Override
        protected Parameters createParams() {
            return new Parameters();
        }

        @Override
        protected RequestString<HttpGet> createRequest(List<NameValuePair> params) {
            return new RequestString<HttpGet>(config.wapiGetBusinessAcountsMy, true, HttpGet.class);
        }

        @Override
        protected List<Account> onResponseDone(String responseResult) {
            List<Account> accounts = new ArrayList<Account>();
            try {
                String responseText = request.execute();
                JSONArray json = new JSONArray(responseText);
                Account account;
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonO = (JSONObject) json.get(i);
                    account = new Account(jsonO);
                    accounts.add(account);
                }

                Log.d("json=" + json);
            } catch (Exception e) {
                logError(e);
            }

            return accounts;
        }

        @Override
        protected abstract void onPostExecute(List<Account> accounts);
    }
}
