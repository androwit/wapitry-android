package fr.fitoussoft.wapitry.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by emmanuel.fitoussi on 15/10/2014.
 */
public class Model {
    private Integer id;
    private String name;

    public Model(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Model(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.name = json.getString("name");

        } catch (JSONException e) {
            Log.d("[TRY]", String.format("error during parse of %s.", Model.class.toString()));
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
