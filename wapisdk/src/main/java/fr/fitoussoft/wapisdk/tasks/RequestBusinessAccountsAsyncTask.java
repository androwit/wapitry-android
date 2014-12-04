package fr.fitoussoft.wapisdk.tasks;

import android.app.Activity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;

import java.util.ArrayList;
import java.util.List;

import fr.fitoussoft.wapisdk.models.Account;
import fr.fitoussoft.wapisdk.requests.RequestString;

/**
 * Created by emmanuel.fitoussi on 30/11/2014.
 */
public abstract class RequestBusinessAccountsAsyncTask extends RequestAsyncTaskBase<List<Account>, String> {

    public RequestBusinessAccountsAsyncTask(Activity origin) {
        super(origin);
    }

    @Override
    protected Parameters createParams() {
        return new Parameters();
    }

    @Override
    protected RequestString<HttpGet> createRequest(List<NameValuePair> params) {
        return new RequestString<HttpGet>(wapiClient, wapiClient.getConfig().wapiGetBusinessAcountsMy, true, HttpGet.class);
    }

    @Override
    protected List<Account> onResponseParsed(String responseResult) {
        List<Account> accounts = new ArrayList<Account>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            accounts = mapper.readValue(responseResult, new TypeReference<List<Account>>() {
            });
        } catch (Exception e) {
            wapiClient.logError(e);
        }

        return accounts;
    }

    @Override
    protected abstract void onPostExecute(List<Account> accounts);
}
