package fr.fitoussoft.wapisdk.tasks;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.fitoussoft.wapisdk.helpers.Log;
import fr.fitoussoft.wapisdk.helpers.WAPIClient;
import fr.fitoussoft.wapisdk.models.Reflection;
import fr.fitoussoft.wapisdk.requests.IRequestBase;
import fr.fitoussoft.wapisdk.requests.RequestString;

/**
* Created by emmanuel.fitoussi on 30/11/2014.
*/
public abstract class RequestNextReflectionsAsyncTask extends RequestAsyncTaskBase<List<Reflection>, String> {

    public final static String PARAM_WAC = "WAC";
    public final static int PAGE_SIZE = 20;
    protected final static String PARAM_SKIP = "skip";
    protected final static String PARAM_TAKE = "take";
    private final static String JSON_FIELD_REFLECTIONS = "reflections";

    public RequestNextReflectionsAsyncTask(WAPIClient wapiClient) {
        super(wapiClient);
    }


    @Override
    protected Parameters createParams() {
        Parameters params = new Parameters();
        params.put(PARAM_WAC, "");
        return params;
    }

    @Override
    protected IRequestBase<String> createRequest(List<NameValuePair> params) {
        int newSkip = wapiClient.nextSkipReflectionRequest;
        wapiClient.nextSkipReflectionRequest += PAGE_SIZE;
        params.add(new BasicNameValuePair(PARAM_SKIP, String.valueOf(newSkip)));
        params.add(new BasicNameValuePair(PARAM_TAKE, String.valueOf(PAGE_SIZE)));
        return new RequestString<HttpGet>(wapiClient, wapiClient.getConfig().wapiSearchReflections, params, true, HttpGet.class);
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
            wapiClient.logError(e);
        }

        return reflections;
    }


    @Override
    protected abstract void onPostExecute(List<Reflection> reflections);
}
