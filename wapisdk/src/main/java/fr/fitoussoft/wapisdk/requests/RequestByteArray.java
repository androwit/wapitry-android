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
public class RequestByteArray<T extends HttpRequestBase> extends RequestBase<T, byte[]> {

    private WapiClient wapiClient;

    public RequestByteArray(WapiClient wapiClient, String url, List<NameValuePair> params, boolean isWithAccessToken, Class<T> httpMethodClass) {
        super(wapiClient, url, params, isWithAccessToken, httpMethodClass);
        this.wapiClient = wapiClient;
    }

    @Override
    protected byte[] castResponse(HttpResponse httpResponse) {
        try {
            return EntityUtils.toByteArray(httpResponse.getEntity());
        } catch (IOException e) {
            wapiClient.logError(e);
            return null;
        }
    }
}
