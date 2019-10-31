package com.example.demo.service;

import com.example.demo.domain.CheckDetailReportMoudle;
import com.example.demo.domain.CheckScheduleReportMoudle;
import com.example.demo.domain.IsaServiceRecord;
import com.example.demo.utils.DateUtils;
import com.example.demo.utils.DateUtils.TimeFormat;
import com.example.demo.utils.DbUtils;
import com.example.demo.utils.PathUtils;
import com.example.demo.utils.TypeUtils;
import com.example.demo.utils.excel.ExcelWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.util.CollectionUtils;
import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Guotong
 */

public class IsaServiceRecordMonthlyReport implements Report {

    private static int LOOP_NUM = 0;

    private final static String REPORT_FILE_NAME = "ISA服务记录确认-Monthly_%s.xlsx";
    private final static String REPORT_NAME = "ISA服务记录确认-Monthly";

    private static String[] REPORT_HEADER = new String[] {"月份", "market", "省份", "城市", "服务商",
            "ISA编号", "ISA姓名", "banner", "商店编码", "商店名称", "ISA类型", "ISA分组", "确认人员账号", "确认人员名称",
            "确认时间", "当月服务小时", "当月已确认服务小时", "当月黄金时段服务小时数", "黄金服务比例"};
    private final static String SQL =
            "select to_char(schedule_date, 'YYYY/MM') as schedule_month,\n"
                    + "       schedule_date,\n"
                    + "       isa_all_info.store_code,\n"
                    + "       isa_all_info.store_name,\n"
                    + "       isa_all_info.seo_market,\n"
                    + "       isa_all_info.banner,\n"
                    + "       isa_all_info.province,\n"
                    + "       isa_all_info.city,\n"
                    + "       isa_all_info.provider_id,\n"
                    + "       isa_all_info.provider_name,\n"
                    + "       isa_all_info.provider_no,\n"
                    + "       isa_all_info.provider_type,\n"
                    + "       isa_all_info.provider_role,\n"
                    + "       isa_all_info.supplier,\n"
                    + "       isa_all_info.service_point_code,\n"
                    + "\n"
                    + "       COALESCE(round(date_part('epoch', check_time - lag(check_time)\n"
                    + "                                                      over (partition by service_time_group_id order by service_time_group_id asc,check_time asc))::NUMERIC /\n"
                    + "                      60), 0)            as duration\n"
                    + "        ,\n"
                    + "       service_time_group_id,\n"
                    + "       schedule_id,\n"
                    + "       check_time,\n"
                    + "       check_type,\n"
                    + "       check_status,\n"
                    + "       is_location_abnormal,\n"
                    + "       approval_by,\n"
                    + "       approval_at,\n"
                    + "       approval_status\n"
                    + "from schedule.ga_store_check_report\n"
                    + "         INNER JOIN bizprocess.isa_all_info\n"
                    + "                    on  schedule.ga_store_check_report.provider_no = isa_all_info.provider_no and\n"
                    + "                        schedule.ga_store_check_report.store_code = isa_all_info.store_code\n"
                    + "where \n"
                    + "schedule_date >= ? and schedule_date <= ? %s\n"
                    + "order by schedule.ga_store_check_report.provider_no, check_time;\n";

    private final static String SCHEDULE_SQL =
            "select provider_no,\n"
                    + "       store_code,\n"
                    + "       schedule_id,\n"
                    + "       start_time,\n"
                    + "       end_time,\n"
                    + "       service_type\n"
                    + "from schedule.ga_schedule_detail\n"
                    + "where \n"
                    + "service_type in ('1','2','3')\n"
                    + "order by schedule.ga_schedule_detail.provider_no;\n";


    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public String export(Map<String, String> params) {

        String excelFile = null;

        String storeCode = params.get("c");
        String providerNo = params.get("n");
        String startDateStr = params.get("s");
        String endDateStr = params.get("e");
        String env = params.get("env");
        Date startDate = TypeUtils.toSqlDate(startDateStr, TimeFormat.LONG_DATE_PATTERN_LINE);
        Date endDate = TypeUtils.toSqlDate(endDateStr, TimeFormat.LONG_DATE_PATTERN_LINE);

        StringBuilder whereStr = new StringBuilder();
        List<Object> sqlParams = new ArrayList<>();
        sqlParams.add(startDate);
        sqlParams.add(endDate);

        if (StringUtils.isNotBlank(storeCode)) {
            whereStr.append(" AND ga_store_check_report.store_code = ?");
            sqlParams.add(storeCode);
        }
        if (StringUtils.isNotBlank(providerNo)) {
            whereStr.append(" AND ga_store_check_report.provider_no = ?");
            sqlParams.add(providerNo);

        }
        String sqlStr = String.format(SQL, whereStr);

        try {
            List<CheckScheduleReportMoudle> listSchedule =
                    DbUtils.querySql(SCHEDULE_SQL, CheckScheduleReportMoudle.class);

            List<IsaServiceRecord> list =
                    DbUtils.querySql(sqlStr, IsaServiceRecord.class, sqlParams.toArray());
            List<List<Object>> rows = new ArrayList();

            Map<String, List<IsaServiceRecord>> groupByProviderNo =
                    list.stream().collect(Collectors.groupingBy(
                            IsaServiceRecord::getProvider_no));
            for (List<IsaServiceRecord> groupByProviderNoValues : groupByProviderNo.values()) {

                Map<String, List<IsaServiceRecord>> groupByStoreCode =
                        groupByProviderNoValues.stream().collect(Collectors.groupingBy(
                                IsaServiceRecord::getStore_code));
                for (List<IsaServiceRecord> valuesStore : groupByStoreCode.values()) {
                    //当日服务分钟
                    int serviceOnDay = 0;
                    //黄金服务小时
                    int goldServiceTime = 0;
                    Map<Long, List<IsaServiceRecord>> groupByScheduleId =
                            valuesStore.stream().collect(Collectors.groupingBy(
                                    IsaServiceRecord::getSchedule_id));
                    List<Object> row = new ArrayList();
                    IsaServiceRecord isaServiceRecord = valuesStore.get(0);
                    row.add(isaServiceRecord.getSchedule_month());
                    row.add(isaServiceRecord.getSeo_market()== null ? "" : isaServiceRecord.getSeo_market());
                    row.add(isaServiceRecord.getProvince()== null ? "" : isaServiceRecord.getProvince());
                    row.add(isaServiceRecord.getCity()== null ? "" : isaServiceRecord.getCity());
                    //服务商
                    row.add(isaServiceRecord.getSupplier()== null ? "" : isaServiceRecord.getSupplier());
                    row.add(isaServiceRecord.getProvider_id()== null ? "" : isaServiceRecord.getProvider_id());
                    row.add(isaServiceRecord.getProvider_name()== null ? "" : isaServiceRecord.getProvider_name());
//                        row.add(isaServiceRecord.getProvider_no()== null ? "" : isaServiceRecord.getProvider_no());
                    row.add(isaServiceRecord.getBanner()== null ? "" : isaServiceRecord.getBanner());
                    row.add(isaServiceRecord.getStore_code());
                    row.add(isaServiceRecord.getStore_name()== null ? "" : isaServiceRecord.getStore_name());
                    row.add(isaServiceRecord.getProvider_type() == null ? "" : isaServiceRecord.getProvider_type());
                    row.add(isaServiceRecord.getProvider_role() == null ? "" : isaServiceRecord.getProvider_role());
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");
                    row.add("");

                    if (!CollectionUtils.isEmpty(valuesStore)) {
                        for (List<IsaServiceRecord> values : groupByScheduleId.values()) {
                            List<CheckScheduleReportMoudle> schduleStoreList = new ArrayList<>();
                            schduleStoreList = listSchedule.stream()
                                    .filter(CheckScheduleReportMoudle -> values.get(0).getSchedule_id().equals(CheckScheduleReportMoudle.getSchedule_id())
                                            && values.get(0).getStore_code().equals(CheckScheduleReportMoudle.getStore_code()))
                                    .collect(Collectors.toList());
                            Map<Long, List<IsaServiceRecord>> groupByServiceTimeGroupId =
                                    values.stream().collect(Collectors.groupingBy(
                                            IsaServiceRecord::getService_time_group_id));
                            Map<Long, List<IsaServiceRecord>> groupByServiceTimeGroupIdSort = new TreeMap<>(
                                    Long::compareTo);
                            groupByServiceTimeGroupId
                                    .forEach(groupByServiceTimeGroupIdSort::put);
                            for (List<IsaServiceRecord> valuesServiceTimeGroupId : groupByServiceTimeGroupIdSort
                                    .values()) {
                                //有效服务时常
                                int serviceTime360 = 0;
                                //一个时间组内开始到结束的有效和无效打卡记录
                                List<IsaServiceRecord> effectiveServiceArr = new ArrayList<IsaServiceRecord>();
                                List<IsaServiceRecord> unEffectiveServiceArr = new ArrayList<IsaServiceRecord>();
                                List<IsaServiceRecord> approvalAccessArr = new ArrayList<IsaServiceRecord>();
                                //审批
                                List<IsaServiceRecord> approvalArr = new ArrayList<>();

                                IsaServiceRecord checkDetailReport = new IsaServiceRecord();
                                checkDetailReport.setCheck_time(null);
                                List<Date> dateList = new ArrayList();

                                for (IsaServiceRecord detailReportMoudle : valuesServiceTimeGroupId) {
                                    if ((detailReportMoudle.getIs_location_abnormal() && detailReportMoudle.getCheck_type() == 1)
                                            || detailReportMoudle.getApproval_status() == 2){
                                        unEffectiveServiceArr.add(detailReportMoudle);
                                    } else {
                                        effectiveServiceArr.add(detailReportMoudle);
                                        if(detailReportMoudle.getApproval_status() == 3){
                                            approvalAccessArr.add(detailReportMoudle);
                                        }
                                    }
                                }
                                if(CollectionUtils.isEmpty(effectiveServiceArr) && !CollectionUtils.isEmpty(unEffectiveServiceArr)){
                                    approvalArr.add(unEffectiveServiceArr.get(0));
                                    goldServiceTime += 0;
                                    //本时间段服务时间
                                    serviceOnDay += 0;
                                }else{
                                    serviceTime360 = DateUtils.getMinNum(effectiveServiceArr.get(0).getCheck_time(),effectiveServiceArr.get(effectiveServiceArr.size() - 1).getCheck_time());
                                    approvalArr.add(effectiveServiceArr.get(0));
//                                    schduleStoreList = listSchedule.stream()
//                                            .filter(CheckScheduleReportMoudle -> effectiveServiceArr.get(0).getSchedule_id().equals(CheckScheduleReportMoudle.getSchedule_id())
//                                                    && effectiveServiceArr.get(0).getStore_code().equals(CheckScheduleReportMoudle.getStore_code()))
//                                            .collect(Collectors.toList());
                                    if(0 < serviceTime360 && serviceTime360 < 360){
                                        // 本日服务结算时间
                                        for(int j = 0; j < schduleStoreList.size(); j ++){
                                            dateList = getEffectiveTime(schduleStoreList.get(j).getStart_time(),schduleStoreList.get(j).getEnd_time(),
                                                    effectiveServiceArr.get(0).getCheck_time(),effectiveServiceArr.get(effectiveServiceArr.size() - 1).getCheck_time()
                                            );
                                            goldServiceTime =
                                                    goldServiceTime +
                                                            getGoldTime(dateList.get(0),dateList.get(1));
                                            serviceOnDay = serviceOnDay + DateUtils.getMinNum(dateList.get(0),dateList.get(1));

                                        }
//                                    serviceOnDay =
//                                            serviceOnDay + serviceTime360;
//                                    goldServiceTime =
//                                            goldServiceTime +
//                                            getGoldTime(effectiveServiceArr.get(0).getCheck_time(),effectiveServiceArr.get(effectiveServiceArr.size() - 1).getCheck_time());

                                    }else{
                                        if(effectiveServiceArr.size() == 2){
                                            //本时间段服务时间
                                            goldServiceTime += 0;
                                            serviceOnDay += 0;
                                        }
                                        if(effectiveServiceArr.size() >= 3){
                                            for(int i = 1; i < effectiveServiceArr.size() - 1; i++){
                                                int checkStart = DateUtils.getMinNum(effectiveServiceArr.get(0).getCheck_time(),effectiveServiceArr.get(i).getCheck_time());
                                                int checkEnd =  DateUtils.getMinNum(effectiveServiceArr.get(i).getCheck_time(),effectiveServiceArr.get(effectiveServiceArr.size() - 1).getCheck_time());
                                                if(checkStart > 120 && checkEnd > 120){
                                                    for(int j = 0; j < schduleStoreList.size(); j ++){
                                                        dateList = getEffectiveTime(schduleStoreList.get(j).getStart_time(),schduleStoreList.get(j).getEnd_time(),
                                                                effectiveServiceArr.get(0).getCheck_time(),effectiveServiceArr.get(effectiveServiceArr.size() - 1).getCheck_time()
                                                        );
                                                        goldServiceTime =
                                                                goldServiceTime +
                                                                        getGoldTime(dateList.get(0),dateList.get(1));
                                                        serviceOnDay = serviceOnDay + DateUtils.getMinNum(dateList.get(0),dateList.get(1));

                                                    }

                                                    break;
                                                }else{
                                                    serviceOnDay += 0;
                                                    goldServiceTime += 0;

                                                }
                                            }
                                        }
                                    }
                                }
//                                if (!CollectionUtils.isEmpty(approvalArr)) {
//                                    if(approvalArr.get(0).getApproval_by() != null){
//                                        row.set(13,approvalArr.get(0).getApproval_by());
//                                    }else{
//                                        row.set(13,"");
//                                    }
//                                    if(approvalArr.get(0).getApproval_at() != null){
//                                        row.set(14,approvalArr.get(0).getApproval_at());
//                                    }else{
//                                        row.set(14,"");
//                                    }
//                                } else {
//                                    row.set(13,"");
//                                    row.set(14,"");
//                                }

                            }

                        }
                    }
                    row.set(13,"");
                    row.set(14,"");
                    row.set(15,df.format((float) serviceOnDay / 60));
                    row.set(16, "");
                    row.set(17, df.format((float) goldServiceTime / 60));
                    if(serviceOnDay > 0 && goldServiceTime > 0){
                        row.set(18, df.format((float) goldServiceTime / serviceOnDay));
                    }else{
                        row.set(18, "");
                    }

                    rows.add(row);
                }
            }

            excelFile = getExcelFile(REPORT_FILE_NAME, params);
            exportExcel(rows, excelFile);

        } catch (
                SQLException e) {
            System.out.println(String.format("%s Exception", this.getClass().getSimpleName()));
            e.printStackTrace();
        }
        return excelFile;
    }

//    //获取排版时间交集
//    private int getScheduleTime(Date startScheduleDate,Date endScheduleDate,Date startEffectiveDate,Date endEffectiveDate) {
//        Date startMax = new Date();
//        Date endMin = new Date();
//        int goldServiceMin = 0;
//        if (startEffectiveDate.compareTo(endScheduleDate) < 0 && endEffectiveDate.compareTo(startScheduleDate) > 0 ) {
//            startMax = startEffectiveDate.compareTo(startScheduleDate) > 0 ? startEffectiveDate : startScheduleDate;
//            endMin = endEffectiveDate.compareTo(endScheduleDate) > 0 ? endScheduleDate : endEffectiveDate;
//            goldServiceMin += DateUtils.getMinNum(startMax, endMin);
//        }
//        return goldServiceMin;
//    }

    private List<Date> getEffectiveTime(Date startScheduleDate,Date endScheduleDate,Date startEffectiveDate,Date endEffectiveDate) {
        List<Date> dateList = new ArrayList<>();
        Date startMax = new Date();
        Date endMin = new Date();
        if (startEffectiveDate.compareTo(endScheduleDate) < 0 && endEffectiveDate.compareTo(startScheduleDate) > 0 ) {
            startMax = startEffectiveDate.compareTo(startScheduleDate) > 0 ? startEffectiveDate : startScheduleDate;
            endMin = endEffectiveDate.compareTo(endScheduleDate) > 0 ? endScheduleDate : endEffectiveDate;
        }
        dateList.add(0,startMax);
        dateList.add(1,endMin);
        return dateList;
    }



    //获取黄金时间
    private int getGoldTime(Date startDate,Date endDate) {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(startDate);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(endDate);
        int yearStart = calendarStart.get(Calendar.YEAR); // 获取年份
        int monthStart = calendarStart.get(Calendar.MONTH) + 1; // 获取月份
        int dayStart = calendarStart.get(Calendar.DATE);
        int yearEnd = calendarEnd.get(Calendar.YEAR); // 获取年份
        int monthEnd = calendarEnd.get(Calendar.MONTH) + 1; // 获取月份
        int dayEnd = calendarEnd.get(Calendar.DATE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startOne = new Date();
        Date endOne = new Date();
        Date startTwo = new Date();
        Date endTwo = new Date();
        try {
            startOne = sdf.parse(String.valueOf(yearStart) + "-" + String.valueOf(monthStart) + "-" + String.valueOf(dayStart) + " 14:00:00");
            endOne = sdf.parse(String.valueOf(yearStart) + "-" + String.valueOf(monthStart) + "-" + String.valueOf(dayStart) + " 22:00:00");
            startTwo = sdf.parse(String.valueOf(yearEnd) + "-" + String.valueOf(monthEnd) + "-" + String.valueOf(dayEnd) + " 14:00:00");
            endTwo = sdf.parse(String.valueOf(yearEnd) + "-" + String.valueOf(monthEnd) + "-" + String.valueOf(dayEnd) + " 22:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 跨天
        Date startMax = new Date();
        Date endMin = new Date();
        int goldServiceMin = 0;
        if(dayEnd != dayStart){
            startMax = startDate.compareTo(startOne) > 0 ? startDate : startOne;
            goldServiceMin += DateUtils.getMinNum(startMax,endOne) > 0 ? DateUtils.getMinNum(startMax,endOne):0;
            endMin = endDate.compareTo(endTwo) > 0 ? endTwo : endDate;
            goldServiceMin += DateUtils.getMinNum(startTwo,endMin) > 0 ? DateUtils.getMinNum(startTwo,endMin):0;
        }else{
            if (startDate.compareTo(endTwo) < 0 && endDate.compareTo(startOne) > 0 ) {
                startMax = startDate.compareTo(startOne) > 0 ? startDate : startOne;
                endMin = endDate.compareTo(endTwo) > 0 ? endTwo : endDate;
                goldServiceMin += DateUtils.getMinNum(startMax, endMin);
            }
        }
        return goldServiceMin;
    }

    private void exportExcel(List<List<Object>> rows, String excelFile) {
        // 创建写excel实例
        ExcelWriter excelWriter = new ExcelWriter();
        String file = StringUtils.join(PathUtils.getAppPath(), excelFile);
        try {
            // 创建excel
            excelWriter.openExcel(new File(file));
            // 创建sheet
            excelWriter.createSheet(REPORT_NAME);

            // 创建一行
            excelWriter.createRow((row, wb) -> {
                for (int i = 0; i < REPORT_HEADER.length; i++) {
                    // 创建列 单元格
                    Cell cell = row.createCell(i);
                    cell.setCellValue(REPORT_HEADER[i]);
                }
            });
            // 设置列宽
            excelWriter.setColumnWidth(0, REPORT_HEADER.length, 3200);
            excelWriter.setColumnWidth(REPORT_HEADER.length - 1, 5600);

            // 报表数据
            rows.forEach(item -> {
                excelWriter.createRow((row, wb) -> {
                    for (int i = 0; i < item.size(); i++) {
                        Object o = item.get(i);
                        Cell cell = row.createCell(i);
                        cell.setCellValue(o.toString());
                    }
                });
            });

            System.out.println(String.format("success(%s)", file));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            excelWriter.closeQuietly();
        }
    }
}
