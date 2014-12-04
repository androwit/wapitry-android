package fr.fitoussoft.wapisdk.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.fitoussoft.wapisdk.IWapiApplication;
import fr.fitoussoft.wapisdk.helpers.WapiClient;
import fr.fitoussoft.wapisdk.requests.IRequestBase;

/**
 * Created by emmanuel.fitoussi on 30/11/2014.
 */
public abstract class RequestAsyncTaskBase<V, R> extends AsyncTask<Void, Integer, V> {

    protected WapiClient wapiClient;
    protected IRequestBase<R> request;
    protected Parameters params;
    protected Activity origin;

    public RequestAsyncTaskBase(Activity origin) {
        this.origin = origin;
        this.wapiClient = ((IWapiApplication) origin.getApplication()).getWapiClient();
        this.params = createParams();
    }

    public Activity getOrigin() {
        return origin;
    }

    public WapiClient getWapiClient() {
        return wapiClient;
    }

    public Parameters getParams() {
        return params;
    }

    /**
     * Creates the request.
     *
     * @param params Public parameters.
     * @return The request.
     */
    protected abstract IRequestBase<R> createRequest(List<NameValuePair> params);

    /**
     * Create public parameters.
     *
     * @return Parameters
     */
    protected abstract Parameters createParams();

    @Override
    protected void onPreExecute() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (String key : this.params.keySet()) {
            params.add(new BasicNameValuePair(key, this.params.get(key)));
        }

        this.request = createRequest(params);

        if (!request.isWithAccessToken()) {
            super.onPreExecute();
            return;
        }

        wapiClient.verifyAuthentication(this);
    }

    public void onAuthenticated() {
        super.onPreExecute();
    }

    @Override
    protected V doInBackground(Void... voids) {
        R responseResult = request.execute();
        return onResponseParsed(responseResult);
    }

    @Override
    protected abstract void onPostExecute(V result);

    /**
     * Triggered when response is parsed.
     *
     * @param responseResult The response to parse.
     * @return The parsed response.
     */
    protected abstract V onResponseParsed(R responseResult);

    public class Parameters extends HashMap<String, String> {
    }
}
