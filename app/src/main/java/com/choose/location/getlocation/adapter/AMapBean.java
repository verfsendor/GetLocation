package com.choose.location.getlocation.adapter;

/**
 * Created by xuzhendong on 2018/4/23.
 */


public class AMapBean {
    private String address;
    private String addressDetail;
    private boolean choose;
    public AMapBean(String address, String addressDetail){
        this.address = address;
        this.addressDetail = addressDetail;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }
}
