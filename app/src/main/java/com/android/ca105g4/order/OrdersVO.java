package com.android.ca105g4.order;

import java.io.Serializable;
import java.sql.Date;

public class OrdersVO implements Serializable {
    private String ordID;		//訂單編號
    private String memID;		//會員編號
    private String braID;		//分店編號
    private Integer numOfRoom;	//房間數量
    private Integer ordType;	//訂單種類
    private Integer numOfGuest;	//入住人數
    private Integer amount;		//總金額
    private Integer bond;		//訂金
    private Integer payment;	//付款方式
    private Integer ordState; 	//訂單狀態
    private Date ordTime;		//訂單成立時間

    public OrdersVO() {
        super();
    }

    public String getOrdID() {
        return ordID;
    }

    public void setOrdID(String ordID) {
        this.ordID = ordID;
    }

    public String getMemID() {
        return memID;
    }

    public void setMemID(String memID) {
        this.memID = memID;
    }

    public String getBraID() {
        return braID;
    }

    public void setBraID(String braID) {
        this.braID = braID;
    }

    public Integer getNumOfRoom() {
        return numOfRoom;
    }

    public void setNumOfRoom(Integer numOfRoom) {
        this.numOfRoom = numOfRoom;
    }

    public Integer getOrdType() {
        return ordType;
    }

    public void setOrdType(Integer ordType) {
        this.ordType = ordType;
    }

    public Integer getNumOfGuest() {
        return numOfGuest;
    }
    public void setNumOfGuest(Integer numOfGuest) {
        this.numOfGuest = numOfGuest;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getBond() {
        return bond;
    }

    public void setBond(Integer bond) {
        this.bond = bond;
    }

    public Integer getPayment() {
        return payment;
    }

    public void setPayment(Integer payment) {
        this.payment = payment;
    }

    public Integer getOrdState() {
        return ordState;
    }

    public void setOrdState(Integer ordState) {
        this.ordState = ordState;
    }

    public Date getOrdTime() {
        return ordTime;
    }

    public void setOrdTime(Date ordTime) {
        this.ordTime = ordTime;
    }
}
