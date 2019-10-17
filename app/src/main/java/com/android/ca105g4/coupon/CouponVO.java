package com.android.ca105g4.coupon;

import java.io.Serializable;

public class CouponVO implements Serializable {

    private String cpnID;         // 優惠券編號
    //	private byte[] cpnPic;        // 優惠券圖片
    private Integer discount;     // 優惠金額
    private Integer quantity;     // 數量
    private Integer appQuantity;  // 原本數量

    public CouponVO() {
        super();
    }

    public String getCpnID() {
        return cpnID;
    }

    public void setCpnID(String cpnID) {
        this.cpnID = cpnID;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getAppQuantity() {
        return appQuantity;
    }

    public void setAppQuantity(Integer appQuantity) {
        this.appQuantity = appQuantity;
    }
}
