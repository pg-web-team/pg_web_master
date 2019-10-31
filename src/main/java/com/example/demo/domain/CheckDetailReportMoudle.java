package com.example.demo.domain;

import java.util.Date;
import lombok.Data;

@Data
public class CheckDetailReportMoudle {

    private String schedule_month;
    private Date schedule_date;
    private String store_code;
    private String store_name;
    private String seo_market;
    private String banner;
    private String city;
    private String supplier;
    private String provider_id;
    private String provider_name;
    private String provider_no;
    private String provider_type;
    private String provider_role;
    private Integer duration;
    private Long service_time_group_id;
    private Long schedule_id;
    private Date check_time;
    private Integer check_status;
    private Boolean is_location_abnormal;
    private Integer approval_status;
    private Integer check_type;
    private String service_point_code;
    private Date start_time;
    private Date end_time;
}
