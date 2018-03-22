package com.github.jc.cryptopea.Models;

/**
 * Created by Owner on 3/21/2018.
 */

public class RewardTransactionItem {
    private String identifier;
    private String remarks;
    private String status;
    private String time;
    private String date;
    private String value;

    public RewardTransactionItem(String identifier, String remarks, String status, String time, String date, String value){
        this.identifier = identifier;
        this.remarks = remarks;
        this.status = status;
        this.time = time;
        this.date = date;
        this.value = value;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
