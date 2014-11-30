package fr.fitoussoft.wapisdk.tasks;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.fitoussoft.wapisdk.helpers.WAPIClient;
import fr.fitoussoft.wapisdk.requests.IRequestBase;

/**
* Created by emmanuel.fitoussi on 30/11/2014.
*/
public abstract class RequestAsyncTaskBase<V, R> extends AsyncTask<Void, Integer, V> {

    protected WAPIClient wapiClient;
    protected IRequestBase<R> request;
    protected Parameters params;

    public RequestAsyncTaskBase(WAPIClient wapiClient) {
        this.wapiClient = wapiClient;
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
