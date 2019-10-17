package com.android.ca105g4.roomType;

import java.io.Serializable;

public class RoomTypeBra implements Serializable{

	private String rtID;			//房型編號
	private String braID;			//分店編號
	private String rtName;			//房型名稱
//	private byte[] rtPic;			//房型圖片
	private String rtIntro;			//房型介紹
	private Integer rtMinimum;		//人數
	private Integer rtLimit;		//上限人數
	private Integer weeklyPrice;	//平日價格
	private Integer holidayPrice;	//假日價格
	private String balance;			//剩餘房間數
	private Integer total;			//總房間數
	
	private String  braName;
	
	private Integer rooms;

	public String getRtID() {
		return rtID;
	}

	public void setRtID(String rtID) {
		this.rtID = rtID;
	}

	public String getBraID() {
		return braID;
	}

	public void setBraID(String braID) {
		this.braID = braID;
	}

	public String getRtName() {
		return rtName;
	}

	public void setRtName(String rtName) {
		this.rtName = rtName;
	}

	public String getRtIntro() {
		return rtIntro;
	}

	public void setRtIntro(String rtIntro) {
		this.rtIntro = rtIntro;
	}

	public Integer getRtMinimum() {
		return rtMinimum;
	}

	public void setRtMinimum(Integer rtMinimum) {
		this.rtMinimum = rtMinimum;
	}

	public Integer getRtLimit() {
		return rtLimit;
	}

	public void setRtLimit(Integer rtLimit) {
		this.rtLimit = rtLimit;
	}

	public Integer getWeeklyPrice() {
		return weeklyPrice;
	}

	public void setWeeklyPrice(Integer weeklyPrice) {
		this.weeklyPrice = weeklyPrice;
	}

	public Integer getHolidayPrice() {
		return holidayPrice;
	}

	public void setHolidayPrice(Integer holidayPrice) {
		this.holidayPrice = holidayPrice;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getBraName() {
		return braName;
	}

	public void setBraName(String braName) {
		this.braName = braName;
	}

	public Integer getRooms() {
		return rooms;
	}

	public void setRooms(Integer rooms) {
		this.rooms = rooms;
	}
}
