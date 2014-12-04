package fr.fitoussoft.wapisdk.models.reflections.data;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;

/**
 * Created by emmanuel.fitoussi on 03/12/2014.
 * [OK]
 */
@JsonTypeName("ReflectionDataUnitOfProd")
public class ReflectionDataUnitOfProd extends ReflectionDataBase {

    private Number stateIndex;
    private String stateIndexDate;
    private Number instantPower;
    private Boolean coupling;
    private Map<String, Number> sequence;
    private String sequenceDate;

    public Number getStateIndex() {
        return stateIndex;
    }

    public void setStateIndex(Number stateIndex) {
        this.stateIndex = stateIndex;
    }

    public String getStateIndexDate() {
        return stateIndexDate;
    }

    public Number getInstantPower() {
        return instantPower;
    }

    public Boolean getCoupling() {
        return coupling;
    }

    public Map<String, Number> getSequence() {
        return sequence;
    }

    public void setSequence(Map<String, Number> sequence) {
        this.sequence = sequence;
    }

    public String getSequenceDate() {
        return sequenceDate;
    }
}
