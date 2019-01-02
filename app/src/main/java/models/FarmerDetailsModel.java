package models;

import io.realm.RealmObject;

public class FarmerDetailsModel extends RealmObject {

    private String farmerName;
    private String farmerPhonenumber;
    private String harvestDate;
    private String farmerAddress;
    private String farmerUnit;
    private String farmerPrice;

    private Boolean contactCheckBox;

    public String getFarmerUnit() {
        return farmerUnit;
    }

    public void setFarmerUnit(String farmerUnit) {
        this.farmerUnit = farmerUnit;
    }

    public String getFarmerPrice() {
        return farmerPrice;
    }

    public void setFarmerPrice(String farmerPrice) {
        this.farmerPrice = farmerPrice;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getFarmerPhonenumber() {
        return farmerPhonenumber;
    }

    public void setFarmerPhonenumber(String farmerPhonenumber) {
        this.farmerPhonenumber = farmerPhonenumber;
    }

    public String getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(String harvestDate) {
        this.harvestDate = harvestDate;
    }

    public String getFarmerAddress() {
        return farmerAddress;
    }

    public void setFarmerAddress(String farmerAddress) {
        this.farmerAddress = farmerAddress;
    }

    public Boolean getContactCheckBox() {
        return contactCheckBox;
    }

    public void setContactCheckBox(Boolean contactCheckBox) {
        this.contactCheckBox = contactCheckBox;
    }

}
