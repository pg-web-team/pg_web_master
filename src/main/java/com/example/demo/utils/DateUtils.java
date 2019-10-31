/**
 * 
 */
package com.example.demo.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 */
public final class DateUtils {

    private DateUtils() {
    }

    /**
     * String 转化为 LocalDateTime
     *
     * @param timeStr 被转化的字符串
     * @param timeFormat 转化的时间格式
     * @return LocalDateTime
     */
    public static LocalDateTime getLocalDateTime(String timeStr, TimeFormat timeFormat) {
        return LocalDateTime.parse(timeStr, timeFormat.formatter);
    }

    public static Date getDateTime(String timeStr, TimeFormat timeFormat) {
        LocalDateTime dt = LocalDateTime.parse(timeStr, timeFormat.formatter);

        ZonedDateTime zdt = dt.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * LocalDateTime 时间转 String
     *
     * @param time LocalDateTime
     * @param format 时间格式
     * @return String
     */
    public static String parseTime(LocalDateTime time, TimeFormat format) {
        return format.formatter.format(time);
    }

    public static String parseTime(Date date, TimeFormat format) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return format.formatter.format(localDateTime);
    }

    /**
     * 获取当前时间
     *
     * @param timeFormat 时间格式
     * @return
     */
    public static String getCurrentDateTime(TimeFormat timeFormat) {
        return timeFormat.formatter.format(LocalDateTime.now());
    }

    public enum TimeFormat {
        /**
         * 时间格式: yyyy-MM-dd HH
         */
        SHORT_DATE_HOUR_PATTERN("yyyy-MM-dd HH"),

        // 短时间格式 年月日
        /**
         * 时间格式：yyyy-MM-dd
         */
        SHORT_DATE_PATTERN_LINE("yyyy-MM-dd"),
        /**
         * 时间格式：yyyy/MM/dd
         */
        SHORT_DATE_PATTERN_SLASH("yyyy/MM/dd"),
        /**
         * 时间格式：yyyy\\MM\\dd
         */
        SHORT_DATE_PATTERN_DOUBLE_SLASH("yyyy\\MM\\dd"),
        /**
         * 时间格式：yyyyMMdd
         */
        SHORT_DATE_PATTERN_NONE("yyyyMMdd"),
        
        /**
         * 时间格式：yyyyMMdd
         */
        LONG_DATE_PATTERN_WITH_MILSEC_NONE_SPACE("yyyyMMddHHmmssSSS"),

        // 长时间格式 年月日时分秒
        /**
         * 时间格式：yyyy-MM-dd HH:mm:ss
         */
        LONG_DATE_PATTERN_LINE("yyyy-MM-dd HH:mm:ss"),

        /**
         * 时间格式：yyyy/MM/dd HH:mm:ss
         */
        LONG_DATE_PATTERN_SLASH("yyyy/MM/dd HH:mm:ss"),
        /**
         * 时间格式：yyyy\\MM\\dd HH:mm:ss
         */
        LONG_DATE_PATTERN_DOUBLE_SLASH("yyyy\\MM\\dd HH:mm:ss"),
        /**
         * 时间格式：yyyyMMdd HH:mm:ss
         */
        LONG_DATE_PATTERN_NONE("yyyyMMdd HH:mm:ss"),
        // 长时间格式 年月日时分秒 带毫秒
        LONG_DATE_PATTERN_WITH_MILSEC_LINE("yyyy-MM-dd HH:mm:ss.SSS"),
        LONG_DATE_PATTERN_WITH_MILSEC_SLASH("yyyy/MM/dd HH:mm:ss.SSS"),
        LONG_DATE_PATTERN_WITH_MILSEC_DOUBLE_SLASH("yyyy\\MM\\dd HH:mm:ss.SSS"),
        LONG_DATE_PATTERN_WITH_MILSEC_NONE("yyyyMMdd HH:mm:ss.SSS");

        private transient DateTimeFormatter formatter;

        TimeFormat(String pattern) {
            formatter = DateTimeFormatter.ofPattern(pattern);

        }
    }

    /**
     * 获取年 - YYYY
     *
     * @param date
     * @return String
     */
    public static String getYear(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return String.valueOf(localDateTime.getYear());
    }

    /**
     * 获取每月第一天 - YYYY-MM-01
     *
     * @param date
     * @return String
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setTimeZero(calendar);

        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTime();
    }

    private static void setTimeZero(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static Date getFirstDayOfWeek(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setTimeZero(calendar);

        // 周一是一周第一天，周日是最后一天
        calendar.add(Calendar.DATE, -1 * calendar.get(Calendar.DAY_OF_WEEK) + 2);
        return calendar.getTime();
    }

    /**
     * 比较时间是否在时间段内
     *
     * @return true在时间段内，false不在时间段内
     */
    public static boolean dateBetween(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 获取 获取某年某月 所有日期
     */
    public static List<Date> getMonthFullDay(int year, int month) {
        List<Date> fullDayList = new ArrayList<>(32);
        // 获得当前日期对象
        Calendar cal = Calendar.getInstance();
        cal.clear();// 清除信息
        cal.set(Calendar.YEAR, year);
        // 1月从0开始
        cal.set(Calendar.MONTH, month - 1);
        // 当月1号
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int count = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int j = 1; j <= count; j++) {
            fullDayList.add(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return fullDayList;
    }

    /**
     * 判断2个时间相差多少分
     *
     * @param begin 开始时间
     * @param end 结束时间
     * @Exception
     */
    public static Integer getMinNum(Date begin, Date end) {
        long between = (end.getTime() - begin.getTime()) / 1000;
        long min = between / 60;
        return Math.toIntExact(min);
    }

    
//    public static String randomDay(int begin, int end) {
//        int randInt = RandomUtils.nextInt(0, 120);
//        LocalDate localDate = LocalDate.now();
//        localDate = localDate.plusDays(randInt);
//        ZoneId zone = ZoneId.systemDefault();
//        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
//        java.util.Date date = Date.from(instant);
//        return parseTime(date, TimeFormat.SHORT_DATE_PATTERN_LINE);
//    }
    
}
