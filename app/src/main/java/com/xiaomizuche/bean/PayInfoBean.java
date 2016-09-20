package com.xiaomizuche.bean;

/**
 * 支付接口返回值
 */
public class PayInfoBean {

    private double amount;

    private String orderInfo;

    public PayInfoBean() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
}
