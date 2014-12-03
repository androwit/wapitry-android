package fr.fitoussoft.wapisdk.tasks;

import android.app.Activity;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.fitoussoft.wapisdk.helpers.Log;
import fr.fitoussoft.wapisdk.helpers.WapiClient;
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
    protected List<Account> onResponseDone(String responseResult) {
        List<Account> accounts = new ArrayList<Account>();
        try {
            JSONArray json = new JSONArray(responseResult);
            Account account;
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonO = (JSONObject) json.get(i);
                account = new Account(jsonO);
                accounts.add(account);
            }

            Log.d("json=" + json);
        } catch (Exception e) {
            wapiClient.logError(e);
        }

        return accounts;
    }

    @Override
    protected abstract void onPostExecute(List<Account> accounts);
}
