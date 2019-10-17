package com.android.ca105g4.order;

import java.io.Serializable;
import java.sql.Date;

public class OrderDetailData implements Serializable {

    private Integer odID;		//訂單明細編號
    private String roomID;		//房間編號
    private String ordID;		//訂單編號
    private String rtID;		//房型編號
    private Date checkIn;		//住房日期
    private Date checkOut;		//退房日期
    private String rtName;		//房型名稱
    private Double evaluates;	//評價
    private Integer special;	//特殊需求(加床)

    private Integer rooms;      //暫存間數

    public Integer getOdID() {
        return odID;
    }

    public void setOdID(Integer odID) {
        this.odID = odID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getOrdID() {
        return ordID;
    }

    public void setOrdID(String ordID) {
        this.ordID = ordID;
    }

    public String getRtID() {
        return rtID;
    }

    public void setRtID(String rtID) {
        this.rtID = rtID;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    public String getRtName() {
        return rtName;
    }

    public void setRtName(String rtName) {
        this.rtName = rtName;
    }

    public Double getEvaluates() {
        return evaluates;
    }

    public void setEvaluates(Double evaluates) {
        this.evaluates = evaluates;
    }

    public Integer getSpecial() {
        return special;
    }

    public void setSpecial(Integer special) {
        this.special = special;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }
}
