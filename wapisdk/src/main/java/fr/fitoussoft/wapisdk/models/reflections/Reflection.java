package fr.fitoussoft.wapisdk.models.reflections;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fr.fitoussoft.wapisdk.models.Model;
import fr.fitoussoft.wapisdk.models.reflections.data.ReflectionDataBase;

/**
 * Created by emmanuel.fitoussi on 15/10/2014.
 * [OK]
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "wType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReflectionGeneric.class, name = "ReflectionGeneric"),
        @JsonSubTypes.Type(value = ReflectionState.class, name = "ReflectionState"),
        @JsonSubTypes.Type(value = ReflectionAnalogic.class, name = "ReflectionAnalogic"),
        @JsonSubTypes.Type(value = ReflectionDigital.class, name = "ReflectionDigital"),
        @JsonSubTypes.Type(value = ReflectionText.class, name = "ReflectionText"),
        @JsonSubTypes.Type(value = ReflectionPlanning.class, name = "ReflectionPlanning"),
        @JsonSubTypes.Type(value = ReflectionUnitOfProd.class, name = "ReflectionUnitOfProd"),
        @JsonSubTypes.Type(value = ReflectionSystem.class, name = "ReflectionSystem"),
        @JsonSubTypes.Type(value = ReflectionEwon.class, name = "ReflectionEwon")
})
public abstract class Reflection extends Model {

    private Number datasCount;
    private String status;
    private String description;
    private Number ltuid;
    private Number infrastructureID;
    @JsonProperty("class")
    private String className;
    private String label;
    private String updateDate;
    private boolean canEvent;
    private boolean canCommand;
    private Number size;
    private String creationDate;
    private String nextCall;
    private String exchangeDate;

    private ReflectionDataBase lastData;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Number getInfrastructureID() {
        return infrastructureID;
    }

    public Number getDatasCount() {
        return datasCount;
    }

    public boolean getCanCommand() {
        return canCommand;
    }

    public String getStatus() {
        return status;
    }

    public Number getLtuid() {
        return ltuid;
    }

    public void setLtuid(Number ltuid) {
        this.ltuid = ltuid;
    }

    public ReflectionDataBase getLastData() {
        return lastData;
    }

    public String getClassName() {
        return className;
    }

    public String getLabel() {
        return label;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public boolean getCanEvent() {
        return canEvent;
    }

    public Number getSize() {
        return size;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getNextCall() {
        return nextCall;
    }

    public String getExchangeDate() {
        return exchangeDate;
    }

}
