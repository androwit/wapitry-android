package fr.fitoussoft.wapisdk.models;

import org.json.JSONException;
import org.json.JSONObject;

import fr.fitoussoft.wapisdk.helpers.Log;

/**
 * Created by emmanuel.fitoussi on 15/10/2014.
 */
public class Reflection extends Model {

    public String className;

    public Reflection(JSONObject json) {
        super(json);
    }

    public String getClassName() {
        return className;
    }

    @Override
    protected void parse(JSONObject json) {
        super.parse(json);
        try {
            this.className = json.getString("class");
        } catch (JSONException e) {
            Log.d(String.format("error during parse of %s.", Reflection.class));
        }
    }
}
