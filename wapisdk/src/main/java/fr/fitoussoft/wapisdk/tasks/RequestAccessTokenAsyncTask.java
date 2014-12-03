package fr.fitoussoft.wapisdk.tasks;

import android.app.Activity;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import fr.fitoussoft.wapisdk.helpers.WapiClient;
import fr.fitoussoft.wapisdk.requests.RequestString;

/**
* Created by emmanuel.fitoussi on 30/11/2014.
*/
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

    public RequestAccessTokenAsyncTask(Activity origin) {
        super(origin);
    }

    @Override
    protected Parameters createParams() {
        Parameters params = new Parameters();
        params.put(PARAM_CODE, "");
        return params;
    }

    @Override
    protected RequestString<HttpPost> createRequest(List<NameValuePair> params) {
        params.add(new BasicNameValuePair(PARAM_CLIENT_ID, wapiClient.getConfig().clientId));
        params.add(new BasicNameValuePair(PARAM_CLIENT_SECRET, wapiClient.getConfig().clientSecret));
        params.add(new BasicNameValuePair(PARAM_GRANT_TYPE, PARAM_AUTHORIZATION_CODE));
        params.add(new BasicNameValuePair(PARAM_REDIRECT_URI, wapiClient.getConfig().redirectUrl));
        return new RequestString<HttpPost>(wapiClient, wapiClient.getConfig().wapiToken, params, false, HttpPost.class);
    }

    @Override
    protected Boolean onResponseDone(String responseResult) {
        boolean result = false;
        try {
            JSONObject json = new JSONObject(responseResult);
            wapiClient.getToken().setTokens(json.getString(JSON_FIELD_ACCESS_TOKEN),
                    json.getString(JSON_FIELD_REFRESH_TOKEN),
                    json.getInt(JSON_FIELD_EXPIRES_IN));
            result = true;
        } catch (JSONException e) {
            wapiClient.logError(e);
        }

        return result;
    }

    @Override
    protected abstract void onPostExecute(Boolean result);
}
