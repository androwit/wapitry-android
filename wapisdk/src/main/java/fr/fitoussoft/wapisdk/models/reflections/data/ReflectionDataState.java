package fr.fitoussoft.wapisdk.models.reflections.data;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by emmanuel.fitoussi on 03/12/2014.
 * [OK]
 */
@JsonTypeName("ReflectionDataState")
public class ReflectionDataState extends ReflectionDataBase {

    private String value;
    private boolean witness;
    private boolean fault;
    private String valueDate;

    public String getValue() {
        return value;
    }

    public boolean getWitness() {
        return witness;
    }

    public boolean getFault() {
        return fault;
    }

    public String getValueDate() {
        return valueDate;
    }
}
