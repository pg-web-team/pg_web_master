package com.example.demo.domain;

import lombok.Data;

import java.util.Date;

@Data
public class CheckScheduleReportMoudle {
    private Long schedule_id;
    private String store_code;
    private String provider_no;
    private Integer service_type;
    private Date start_time;
    private Date end_time;
}
