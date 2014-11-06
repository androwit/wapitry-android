package fr.fitoussoft.wapisdk.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by emmanuel.fitoussi on 15/10/2014.
 */
public class Reflection extends Model {

    public String getClassName() {
        return className;
    }

    public String className;

    public Reflection(int id, String name, String className) {
        super(id, name);
        this.className = className;
    }

    public Reflection(JSONObject json) {
        super(json);
        try {
            this.className = json.getString("class");
        } catch (JSONException e) {
            Log.d("[TRY]", String.format("error during parse of %s.", Reflection.class));
        }
    }
}
