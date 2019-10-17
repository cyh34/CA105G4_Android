package com.android.ca105g4.member;

import java.io.Serializable;
import java.sql.Date;

public class MemberVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String memID;        //編號
    private String memName;      //姓名
    private String memAcc;       //帳號
    private String memPsw;       //密碼
    private Date memBirth;       //生日
    private String memEmail;     //Email
    private String memTel;       //電話
    private String memAddr;      //地址
    private String memSex;       //性別
    private Date memReg;         //註冊日期
    private String memSkill;     //技能
    private Integer memState;    //狀態
    //	private byte[] memPic;       //照片
    private String memIDcard;    //身分證字號

    public MemberVO() {
        super();
    }

    public String getMemID() {
        return memID;
    }

    public void setMemID(String memID) {
        this.memID = memID;
    }

    public String getMemName() {
        return memName;
    }

    public void setMemName(String memName) {
        this.memName = memName;
    }

    public String getMemAcc() {
        return memAcc;
    }

    public void setMemAcc(String memAcc) {
        this.memAcc = memAcc;
    }

    public String getMemPsw() {
        return memPsw;
    }

    public void setMemPsw(String memPsw) {
        this.memPsw = memPsw;
    }

    public Date getMemBirth() {
        return memBirth;
    }

    public void setMemBirth(Date memBirth) {
        this.memBirth = memBirth;
    }

    public String getMemEmail() {
        return memEmail;
    }

    public void setMemEmail(String memEmail) {
        this.memEmail = memEmail;
    }

    public String getMemTel() {
        return memTel;
    }

    public void setMemTel(String memTel) {
        this.memTel = memTel;
    }

    public String getMemAddr() {
        return memAddr;
    }

    public void setMemAddr(String memAddr) {
        this.memAddr = memAddr;
    }

    public String getMemSex() {
        return memSex;
    }

    public void setMemSex(String memSex) {
        this.memSex = memSex;
    }

    public Date getMemReg() {
        return memReg;
    }

    public void setMemReg(Date memReg) {
        this.memReg = memReg;
    }

    public String getMemSkill() {
        return memSkill;
    }

    public void setMemSkill(String memSkill) {
        this.memSkill = memSkill;
    }

    public Integer getMemState() {
        return memState;
    }

    public void setMemState(Integer memState) {
        this.memState = memState;
    }

    public String getMemIDcard() {
        return memIDcard;
    }

    public void setMemIDcard(String memIDcard) {
        this.memIDcard = memIDcard;
    }
}
