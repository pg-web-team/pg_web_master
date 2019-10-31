package com.example.demo.domain;

import java.util.Date;
import lombok.Data;

@Data
public class IsaScheduleReportMoudle {

    private String schedule_month;
    private Long schedule_id;
    private String province;
    private String city;
    private String supplier;
    private String store_name;
    private String provider_id;
    private String provider_name;
    private String provider_role;
    private String contact;
    private Date schedule_date;
    private Integer service_status;
    private Integer service_type;
    private Date start_time;
    private Date end_time;
    private Integer service_duration;
    private Date service_end_date;
    private String provider_type;

    //服务商
    private String service_point_code;
    private String store_owner_name;
    private String store_owner_phone;
    private Date transfer_date;
}
