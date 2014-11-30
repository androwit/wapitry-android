package fr.fitoussoft.wapisdk.requests;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

import fr.fitoussoft.wapisdk.helpers.WapiClient;

/**
* Created by emmanuel.fitoussi on 30/11/2014.
*/
public class RequestString<T extends HttpRequestBase> extends RequestBase<T, String> {

    private WapiClient wapiClient;

    public RequestString(WapiClient wapiClient, String url, boolean isWithAccessToken, Class<T> httpMethodClass) {
        super(wapiClient, url, null, isWithAccessToken, httpMethodClass);
        this.wapiClient = wapiClient;
    }

    public RequestString(WapiClient wapiClient, String url, List<NameValuePair> params, boolean isWithAccessToken, Class<T> httpMethodClass) {
        super(wapiClient, url, params, isWithAccessToken, httpMethodClass);
        this.wapiClient = wapiClient;
    }

    @Override
    protected String castResponse(HttpResponse httpResponse) {
        try {
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            wapiClient.logError(e);
        }

        return null;
    }
}
