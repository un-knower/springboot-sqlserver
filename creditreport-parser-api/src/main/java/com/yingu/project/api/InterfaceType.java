package com.yingu.project.api;

public enum InterfaceType {
    /**无银行卡风控-黑名单查询接口*/
    NoneBankAntifraud_BlackList("无银行卡-黑名单查询接口", "1");


    String name;
    String value;

    public String getName() {
        return name;
    }
    public String getValue() {
        return value;
    }

    InterfaceType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    public static String toValue(Status status) {
        return status.getValue();
    }

    public static InterfaceType fromValue(String v) {
        return valueOf(v);
    }
}