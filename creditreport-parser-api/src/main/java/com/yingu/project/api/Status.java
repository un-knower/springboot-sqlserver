package com.yingu.project.api;

public enum Status {
    SUCCESS("SUCCESS", "100"),
    FAILED("FAILED", "200"), //technique failure
    PROCESSING("PROCESSING", "300"),
    MISSING("MISSING", "400"),//business failure
    TIMEOUT("TIMEOUT", "408"),//请求超时
    BAD_REQUEST("BAD_REQUEST","499"),
    CANCELED("CANCELED", "500");//作废：已通过定时任务，将(因为程序异常，状态停留为)PROCESSING(300)的申请重新处理(重发到mq)


    String name;
    String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    Status(String name, String value) {
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

    public static Status fromValue(String v) {
        return valueOf(v);
    }
}