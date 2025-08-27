package com.uom.Software_design_competition.application.constant;

public class LoggingAdviceConstants {

    private LoggingAdviceConstants(){}

    public static final String REQUEST_INITIATED = "HMS_REQUEST_INITIATED|0|REQUEST_METHOD:{}|REQUEST_URI:{}";
    public static final String REQUEST_TERMINATED = "HMS_REQUEST_TERMINATED|{}|REASON:{}";
    public static final String EXCEPTION_STACK_TRACE = "HMS_EXCEPTION|{}|ERROR_MESSAGE:{}|STACK_TRACE:{}";
    public static final String HMS_QUEUE = "HMS_QUEUE|{}|TYPE:{}|MESSAGE:{}";

    // Report Generation Logs
    public static final String REPORT_GENERATION_STARTED = "Starting report generation process...";
    public static final String REPORT_SAVED_TO_DB = "Report entry saved in database. ID: {}";
    public static final String REPORT_SENT_TO_QUEUE = "Report ID {} sent to RabbitMQ queue.";
    public static final String SERVICE_TERMINATION = "NMS_SERVICE_TERMINATED|{}|MESSAGE:{}";

}
