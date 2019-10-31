/**
 * 
 */
package com.example.demo.domain;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Guotong
 *
 */
@Getter
@Setter
public class IsaServiceRecord {
    private SimpleDateFormat df =  new SimpleDateFormat("yyyyMM");

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
    private String province;
    private String approval_by;
    private String approval_at;
    private Date start_time;
    private Date end_time;
    
    public Integer getYearMonth() {
        return Integer.valueOf(df.format(schedule_date));
        
    }
    
}
