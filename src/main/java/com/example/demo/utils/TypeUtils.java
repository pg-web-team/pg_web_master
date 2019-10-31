package com.example.demo.utils;

import com.example.demo.utils.DateUtils.TimeFormat;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Wang,Jingzhu
 *
 */
public interface TypeUtils {

    /**
     * Integer value
     * 
     * @param str
     * @param defaultValue
     * @return
     */
    public static int toIntValue(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }

    }

    /**
     * Long value
     * 
     * @param str
     * @param defaultValue
     * @return
     */
    public static long toLongValue(String str, long defaultValue) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }


    }

    /**
     * Date
     * 
     * @param str
     * @param timeFormat
     * @return
     */
    public static Date toDate(String str, TimeFormat timeFormat) {
        return DateUtils.getDateTime(str, timeFormat);
    }
    
    public static Timestamp toSqlDate(String str, TimeFormat timeFormat) {
        Date date =  toDate(str, timeFormat);
        return new Timestamp(date.getTime());
    }

    public static void main(String[] args) {
        System.out.println(toSqlDate("2010-01-01 11:11:11", TimeFormat.LONG_DATE_PATTERN_LINE));
    }
}
