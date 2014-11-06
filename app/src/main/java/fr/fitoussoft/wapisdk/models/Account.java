package fr.fitoussoft.wapisdk.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by emmanuel.fitoussi on 07/10/2014.
 */
public class Account extends Model {

    private String pictureId;
    private String wac;

    public Account(Integer id, String name, String pictureId, String wac) {
        super(id, name);
        this.pictureId = pictureId;
        this.wac = wac;
    }

    public Account(JSONObject json) {
        super(json);
        try {
            pictureId = json.getString("picture");
            wac = json.getString("wac");
        } catch (JSONException e) {
            Log.d("[TRY]", String.format("error during parse of %s.", Account.class.toString()));
        }
    }

    public String getWac() {
        return wac;
    }

    public String getPictureId() {
        return pictureId;
    }

}
