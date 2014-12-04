package fr.fitoussoft.wapisdk.models.reflections.data;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by emmanuel.fitoussi on 03/12/2014.
 * [OK]
 */
@JsonTypeName("ReflectionDataEwon")
public class ReflectionDataEwon extends ReflectionDataBase {
    private String scenario;
    private Number freeFlashSize;
    private Number freeRAMSize;
    private String executionCode;
    private String executionStatus;

    public String getExecutionCode() {
        return executionCode;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public Number getFreeFlashSize() {
        return freeFlashSize;
    }

    public Number getFreeRAMSize() {
        return freeRAMSize;
    }

}
