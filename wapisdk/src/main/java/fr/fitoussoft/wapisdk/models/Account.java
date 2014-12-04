package fr.fitoussoft.wapisdk.models;

import android.graphics.Bitmap;

/**
 * Created by emmanuel.fitoussi on 07/10/2014.
 */
public class Account extends Model {

    private final static String JSON_FIELD_WAC = "wac";

    private String wac;
    private String status;
    private String ownerAccountID;
    private String operatorAccountID;
    private String dataStoreSize;
    private String url;
    private String updateDate;
    private String creationDate;
    private String hasDataStore;
    private Address address;
    private String description;
    private byte[] pictureBytes;
    private Bitmap pictureBitmap;

    ///

    public Account() {
        super();
    }

    ///

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerAccountID() {
        return ownerAccountID;
    }

    public void setOwnerAccountID(String ownerAccountID) {
        this.ownerAccountID = ownerAccountID;
    }

    public String getOperatorAccountID() {
        return operatorAccountID;
    }

    public void setOperatorAccountID(String operatorAccountID) {
        this.operatorAccountID = operatorAccountID;
    }

    public String getDataStoreSize() {
        return dataStoreSize;
    }

    public void setDataStoreSize(String dataStoreSize) {
        this.dataStoreSize = dataStoreSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }


    public String getHasDataStore() {
        return hasDataStore;
    }

    public void setHasDataStore(String hasDataStore) {
        this.hasDataStore = hasDataStore;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWac() {
        return wac;
    }

    public byte[] getPictureBytes() {
        return pictureBytes;
    }

    public void setPictureBytes(byte[] bytes) {
        pictureBytes = bytes;
    }

    public Bitmap getPictureBitmap() {
        return pictureBitmap;
    }

    public void setPictureBitmap(Bitmap pictureBitmap) {
        this.pictureBitmap = pictureBitmap;
    }
}
