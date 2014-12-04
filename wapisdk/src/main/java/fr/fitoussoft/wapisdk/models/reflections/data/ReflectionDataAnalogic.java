package fr.fitoussoft.wapisdk.models.reflections.data;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by emmanuel.fitoussi on 03/12/2014.
 * [OK]
 */
@JsonTypeName("ReflectionDataAnalogic")
public class ReflectionDataAnalogic extends ReflectionDataBase {

    private Number value;

    private String valueDate;

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public String getValueDate() {
        return valueDate;
    }
}
