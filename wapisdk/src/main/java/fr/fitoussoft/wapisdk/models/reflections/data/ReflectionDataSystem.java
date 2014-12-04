package fr.fitoussoft.wapisdk.models.reflections.data;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by emmanuel.fitoussi on 03/12/2014.
 * [OK]
 */
@JsonTypeName("ReflectionDataSystem")
public class ReflectionDataSystem extends ReflectionDataBase {

    private String scenario;
    private Number freeFlashSize;
    private Number freeRAMSize;
    private Number connectedExt;
    private String option;
    private String location;
    private Number fileID;
    private boolean global;
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

    public Number getConnectedExt() {
        return connectedExt;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public Number getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
