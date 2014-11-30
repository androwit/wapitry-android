package fr.fitoussoft.wapisdk.helpers;

import android.content.res.Resources;

import fr.fitoussoft.wapisdk.R;

/**
* Created by emmanuel.fitoussi on 30/11/2014.
*/
public class Configuration {
    public String clientId;
    public String clientSecret;
    public String wapiAuthorise;
    public String wapiToken;
    public String wapiGetBusinessAcountsMy;
    public String wapiSearchReflections;
    public String wapiLoadPicture;
    public String redirectUrl;

    public Configuration(Resources res, boolean isDebug) {

        this.clientId = res.getString(R.string.client_id);
        this.clientSecret = res.getString(R.string.client_secret);
        this.wapiAuthorise = res.getString(R.string.wapi_authorise);
        this.wapiToken = res.getString(R.string.wapi_token);
        this.wapiGetBusinessAcountsMy = res.getString(R.string.wapi_GetBusinessAccountsMy);
        this.wapiSearchReflections = res.getString(R.string.wapi_SearchReflections);
        this.wapiLoadPicture = res.getString(R.string.wapi_LoadPicture);

        if (isDebug) {
            this.clientId = res.getString(R.string.client_id_beta);
            this.clientSecret = res.getString(R.string.client_secret_beta);
            this.wapiAuthorise = res.getString(R.string.wapi_authorise_beta);
            this.wapiToken = res.getString(R.string.wapi_token_beta);
            this.wapiGetBusinessAcountsMy = res.getString(R.string.wapi_GetBusinessAccountsMy_beta);
            this.wapiSearchReflections = res.getString(R.string.wapi_SearchReflections_beta);
            this.wapiLoadPicture = res.getString(R.string.wapi_LoadPicture_beta);
        }

        this.redirectUrl = res.getString(R.string.redirect_uri);
    }
}
