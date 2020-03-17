package com.example.multyplay;

public class Id {

    private String code;
    private long serialNum;

    public Id() {
    }



//    Constructor made to set id as serial number of currentTimeMillis instead of always counting
    public Id(int anynum) {
        setCode("");
        setSerialNum(System.currentTimeMillis());
    }

    public Id(String code, long serialNum) {
        setCode(code);
        setSerialNum(serialNum);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(long serialNum) {
        this.serialNum = serialNum;
    }
}
