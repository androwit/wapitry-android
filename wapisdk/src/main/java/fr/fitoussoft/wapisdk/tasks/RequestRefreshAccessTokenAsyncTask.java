package fr.fitoussoft.wapisdk.tasks;

import android.app.Activity;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;

import java.util.List;

import fr.fitoussoft.wapisdk.requests.RequestString;

/**
 * Created by emmanuel.fitoussi on 30/11/2014.
 */
public abstract class RequestRefreshAccessTokenAsyncTask extends RequestAccessTokenAsyncTask {

    public RequestRefreshAccessTokenAsyncTask(Activity origin) {
        super(origin);
    }

    @Override
    protected Parameters createParams() {
        Parameters params = new Parameters();
        params.put(PARAM_CLIENT_ID, wapiClient.getConfig().clientId);
        params.put(PARAM_CLIENT_SECRET, wapiClient.getConfig().clientSecret);
        params.put(PARAM_REFRESH_TOKEN, wapiClient.getToken().getRefreshToken());
        params.put(PARAM_GRANT_TYPE, PARAM_REFRESH_TOKEN);
        return params;
    }

    @Override
    protected RequestString<HttpPost> createRequest(List<NameValuePair> params) {
        return new RequestString<HttpPost>(wapiClient, wapiClient.getConfig().wapiToken, params, false, HttpPost.class);
    }

    @Override
    protected abstract void onPostExecute(Boolean result);
}
