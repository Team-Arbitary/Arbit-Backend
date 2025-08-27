package com.uom.Software_design_competition.application.transport.response;

import lombok.Data;

@Data
public class PageDetail {
    private String totalRecords;
    private String pageNumber;
    private String pageElementCount;
}