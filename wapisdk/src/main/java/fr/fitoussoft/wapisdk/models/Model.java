package fr.fitoussoft.wapisdk.models;

import org.json.JSONException;
import org.json.JSONObject;

import fr.fitoussoft.wapisdk.helpers.Log;

/**
 * Created by emmanuel.fitoussi on 15/10/2014.
 */
public class Model {
    private final static String JSON_FIELD_ID = "id";
    private final static String JSON_FIELD_NAME = "name";
    private Integer id;
    private String name;

    public Model() {
    }

    public Model(JSONObject json) {
        parse(json);
    }

    protected void parse(JSONObject json) {
        try {
            this.id = json.getInt(JSON_FIELD_ID);
            this.name = json.getString(JSON_FIELD_NAME);
        } catch (JSONException e) {
            Log.d(String.format("error during parse of %s.", Model.class.toString()));
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
