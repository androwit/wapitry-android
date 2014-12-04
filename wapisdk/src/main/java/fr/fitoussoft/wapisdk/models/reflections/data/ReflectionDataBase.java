package fr.fitoussoft.wapisdk.models.reflections.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fr.fitoussoft.wapisdk.models.Model;

/**
 * Created by emmanuel.fitoussi on 03/12/2014.
 * [OK]
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "wType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReflectionDataState.class, name = "ReflectionDataState"),
        @JsonSubTypes.Type(value = ReflectionDataText.class, name = "ReflectionDataText"),
        @JsonSubTypes.Type(value = ReflectionDataPlanning.class, name = "ReflectionDataPlanning"),
        @JsonSubTypes.Type(value = ReflectionDataAnalogic.class, name = "ReflectionDataAnalogic"),
        @JsonSubTypes.Type(value = ReflectionDataDigital.class, name = "ReflectionDataDigital"),
        @JsonSubTypes.Type(value = ReflectionDataUnitOfProd.class, name = "ReflectionDataUnitOfProd"),
        @JsonSubTypes.Type(value = ReflectionDataSystem.class, name = "ReflectionDataSystem"),
        @JsonSubTypes.Type(value = ReflectionDataEwon.class, name = "ReflectionDataEwon")
})
public abstract class ReflectionDataBase extends Model {
    private String creationDate;
    private String sendDate;
    private String executionDate;
    private String phase;
    private String state;

    public String getCreationDate() {
        return creationDate;
    }

    public String getSendDate() {
        return sendDate;
    }

    public String getExecutionDate() {
        return executionDate;
    }

    public String getPhase() {
        return phase;
    }

    public String getState() {
        return state;
    }
}
