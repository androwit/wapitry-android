package fr.fitoussoft.wapisdk.models;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import fr.fitoussoft.wapisdk.helpers.Log;

/**
 * Created by emmanuel.fitoussi on 07/10/2014.
 */
public class Account extends Model {

    private String pictureId;
    private byte[] pictureBytes;
    private Bitmap picture;
    private String wac;

    public Account(JSONObject json) {
        super(json);
    }

    @Override
    protected void parse(JSONObject json) {
        super.parse(json);

        try {
            pictureId = json.getString("picture");
            wac = json.getString("wac");
        } catch (JSONException e) {
            Log.d(String.format("error during parse of %s.", Account.class.toString()));
        }
    }

    public String getWac() {
        return wac;
    }

    public String getPictureId() {
        return pictureId;
    }

    public byte[] getPictureBytes() {
        return pictureBytes;
    }

    public void setPictureBytes(byte[] bytes) {
        pictureBytes = bytes;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

}
