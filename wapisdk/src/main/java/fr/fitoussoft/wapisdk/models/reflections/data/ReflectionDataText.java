package fr.fitoussoft.wapisdk.models.reflections.data;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by emmanuel.fitoussi on 03/12/2014.
 * [OK]
 */
@JsonTypeName("ReflectionDataText")
public class ReflectionDataText extends ReflectionDataBase {

    private String value;

    private String valueDate;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueDate() {
        return valueDate;
    }
}
