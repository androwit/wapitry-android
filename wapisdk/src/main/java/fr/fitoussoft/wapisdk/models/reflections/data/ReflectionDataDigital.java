package fr.fitoussoft.wapisdk.models.reflections.data;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by emmanuel.fitoussi on 03/12/2014.
 * [OK]
 */
@JsonTypeName("ReflectionDataDigital")
public class ReflectionDataDigital extends ReflectionDataBase {

    private Boolean value;

    private String valueDate;

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public String getValueDate() {
        return valueDate;
    }
}
