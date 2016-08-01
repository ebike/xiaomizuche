package com.xiaomizuche.bean;

/**
 * 短信错误信息
 */
public class SMSResult {

    private int status;
    private String detail;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }
}
