package com.uom.Software_design_competition.application.util.resultenum;

public enum ResponseCodeEnum {
    SUCCESS("2000","Operation Successful"),
    BAD_REQUEST("4000","bad request"),
    LOG_NOT_CONNECTED("5000","operational logs not connected"),
    BAD_REQUEST_INVALID_FIELDS("4000", "Missing or invalid required fields"),
    PARTIAL_SUCCESS("2007", "Partial Success"),
    OPERATION_FAILED("5000", "Operation Failed!"),
    NOT_FOUND("4000","Not found"),
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
