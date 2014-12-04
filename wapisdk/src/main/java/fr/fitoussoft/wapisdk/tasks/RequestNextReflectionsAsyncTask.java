package fr.fitoussoft.wapisdk.tasks;

import android.app.Activity;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import fr.fitoussoft.wapisdk.models.containers.ReflectionContainer;
import fr.fitoussoft.wapisdk.models.reflections.Reflection;
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

    public static int nextSkipReflectionRequest = 0;

    public RequestNextReflectionsAsyncTask(Activity origin) {
        super(origin);
    }


    @Override
    protected Parameters createParams() {
        Parameters params = new Parameters();
        params.put(PARAM_WAC, "");
        return params;
    }

    @Override
    protected IRequestBase<String> createRequest(List<NameValuePair> params) {
        int newSkip = nextSkipReflectionRequest;
        nextSkipReflectionRequest += PAGE_SIZE;
        params.add(new BasicNameValuePair(PARAM_SKIP, String.valueOf(newSkip)));
        params.add(new BasicNameValuePair(PARAM_TAKE, String.valueOf(PAGE_SIZE)));
        return new RequestString<HttpGet>(wapiClient, wapiClient.getConfig().wapiSearchReflections, params, true, HttpGet.class);
    }

    @Override
    protected List<Reflection> onResponseParsed(String response) {
        List<Reflection> reflections = new ArrayList<Reflection>();
        ReflectionContainer container;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            container = mapper.readValue(response, ReflectionContainer.class);
            reflections = container.getReflections();
        } catch (Exception e) {
            wapiClient.logError(e);
        }

        return reflections;
    }


    @Override
    protected abstract void onPostExecute(List<Reflection> reflections);
}
