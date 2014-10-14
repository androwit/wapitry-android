package fr.fitoussoft.wapitry.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by emmanuel.fitoussi on 07/10/2014.
 */
public class Account {

    private int id;
    private String name;
    private String pictureId;
    private String wac;

    public Account(int id, String name, String pictureId, String wac) {
        this.id = id;
        this.name = name;
        this.pictureId = pictureId;
        this.wac = wac;
    }

    public Account(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.name =json.getString("name");
            this.pictureId = json.getString("picture");
            this.wac =json.getString("wac");
        } catch (JSONException e) {
            Log.d("[TRY]", "error during parse account.");
        }
    }

    public String getWac() {
        return wac;
    }

    public String getPictureId() {
        return pictureId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
