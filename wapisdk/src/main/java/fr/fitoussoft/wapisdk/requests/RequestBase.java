package fr.fitoussoft.wapisdk.requests;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.net.URI;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import fr.fitoussoft.wapisdk.helpers.PermisiveSSLSocketFactory;
import fr.fitoussoft.wapisdk.helpers.WAPIClient;

/**
* Created by emmanuel.fitoussi on 30/11/2014.
*/
public abstract class RequestBase<T extends HttpRequestBase, U> implements IRequestBase<U> {
    private WAPIClient wapiClient;
    protected boolean isWithAccessToken = false;
    protected String url;
    protected T httpMethod;
    protected U response;
    protected List<NameValuePair> params;

    public RequestBase(WAPIClient wapiClient, String url, boolean isWithAccessToken, Class<T> httpMethodClass) {
        this(wapiClient, url, null, isWithAccessToken, httpMethodClass);
    }

    public RequestBase(WAPIClient wapiClient, String url, List<NameValuePair> params, boolean isWithAccessToken, Class<T> httpMethodClass) {
        this.wapiClient = wapiClient;
        try {
            this.url = url;
            this.isWithAccessToken = isWithAccessToken;
            httpMethod = httpMethodClass.newInstance();
            this.params = params;

            if (this.isWithAccessToken) {
                httpMethod.addHeader("Authorization", "Bearer " + wapiClient.getToken().getAccessToken());
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
            wapiClient.logError(e);
        }
    }

    private final static HttpClient httpClient = getNewHttpClient();

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
            wapiClient.logError(e);
        }

        return response;
    }

    protected abstract U castResponse(HttpResponse httpResponse);
}
