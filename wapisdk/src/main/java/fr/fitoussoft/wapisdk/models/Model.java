package fr.fitoussoft.wapisdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by emmanuel.fitoussi on 15/10/2014.
 */
public class Model {
    private final static String JSON_FIELD_ID = "id";
    private final static String JSON_FIELD_NAME = "name";
    private final static String JSON_FIELD_WTYPE = "wType";
    private final static String JSON_FIELD_PICTURE = "picture";

    @JsonProperty(JSON_FIELD_WTYPE)
    private String wType;

    @JsonProperty(JSON_FIELD_ID)
    private Number id;

    @JsonProperty(JSON_FIELD_NAME)
    private String name;

    @JsonProperty(JSON_FIELD_PICTURE)
    private String picture;


    public Model() {
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getwType() {
        return wType;
    }

    public Number getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
