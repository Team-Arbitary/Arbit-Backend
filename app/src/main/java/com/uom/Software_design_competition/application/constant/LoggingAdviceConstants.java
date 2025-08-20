package com.uom.Software_design_competition.application.constant;

public class LoggingAdviceConstants {

    private LoggingAdviceConstants(){}

    public static final String REQUEST_INITIATED = "REQUEST_INITIATED|0|REQUEST_METHOD:{}|REQUEST_URI:{}";
    public static final String REQUEST_TERMINATED = "REQUEST_TERMINATED|{}|REASON:{}";
    public static final String EXCEPTION_STACK_TRACE = "EXCEPTION|{}|ERROR_MESSAGE:{}|STACK_TRACE:{}";

}
