package com.uom.Software_design_competition.application.util.resultenum;

public enum ResponseCodeEnum {
    SUCCESS("2000","Operation Successful"),
    BAD_REQUEST("4000","bad request"),
    LOG_NOT_CONNECTED("5000","Records are not connected"),
    BAD_REQUEST_INVALID_FIELDS("4000", "Missing or invalid required fields"),
    INTERNAL_SERVER_ERROR("5000","Internal Server Error");

    private String code;
    private String message;
    ResponseCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
    public String code() {
        return code;
    }
    public String message() { return message; }
}
