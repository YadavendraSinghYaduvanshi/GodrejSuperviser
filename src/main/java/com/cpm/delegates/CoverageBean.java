package com.cpm.delegates;

public class CoverageBean {
    protected int MID;
    protected String storeId;
    protected String remarkis;
    protected String coveargestatus;

    public String getCoveargestatus() {
        return coveargestatus;
    }

    public void setCoveargestatus(String coveargestatus) {
        this.coveargestatus = coveargestatus;
    }

    public String getRemarkis() {
        return remarkis;
    }

    public void setRemarkis(String remarkis) {
        this.remarkis = remarkis;
    }


    protected String Remark;

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    protected String userId;

    protected String inTime;

    protected String outTime;

    protected String visitDate;
    private String latitude;
    private String longitude;
    private String reasonid = "";

    public String getImage02() {
        return image02;
    }

    public void setImage02(String image02) {
        this.image02 = image02;
    }

    private String image02 = "";
    private String reason = "";
    private String status = "N";
    private String image = "";

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMID() {
        return MID;
    }

    public void setMID(int mID) {
        MID = mID;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getReasonid() {
        return reasonid;
    }

    public void setReasonid(String reasonid) {
        this.reasonid = reasonid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    protected String search_storeCODE;

    public String getSearch_storeCODE() {
        return search_storeCODE;
    }

    public void setSearch_storeCODE(String search_storeCODE) {
        this.search_storeCODE = search_storeCODE;
    }

    public String getSearch_storeNAME() {
        return search_storeNAME;
    }

    public void setSearch_storeNAME(String search_storeNAME) {
        this.search_storeNAME = search_storeNAME;
    }

    protected String search_storeNAME;

}
