package com.android.ca105g4.coupon;

import java.io.Serializable;

public class Coupon implements Serializable{
	
	private String cpnID;         // 優惠券編號
//	private byte[] cpnPic;        // 優惠券圖片
	private Integer discount;     // 優惠金額 
	private Integer quantity;     // 數量
	private Integer appQuantity;  // 原本數量

	private String memID;       // 會員編號
	private Integer cpnState;   // 優惠券收藏紀錄
	
	public Coupon() {
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
	
	public String getMemID() {
		return memID;
	}
	
	public void setMemID(String memID) {
		this.memID = memID;
	}
	
	public Integer getCpnState() {
		return cpnState;
	}
	
	public void setCpnState(Integer cpnState) {
		this.cpnState = cpnState;
	}
}
