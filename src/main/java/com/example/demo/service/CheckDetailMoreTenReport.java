package com.example.demo.service;

import com.example.demo.domain.CheckDetailReportMoudle;
import com.example.demo.domain.CheckInServiceMaxMoudle;
import com.example.demo.utils.DateUtils.TimeFormat;
import com.example.demo.utils.DbUtils;
import com.example.demo.utils.PathUtils;
import com.example.demo.utils.TypeUtils;
import com.example.demo.utils.excel.ExcelWriter;
import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.util.CollectionUtils;

/**
 * @author Guotong
 */

public class CheckDetailMoreTenReport implements Report {

    private static int LOOP_NUM = 0;

    private static int IN_SERVICE_MAX_INDEX = 0;

    private final static String REPORT_FILE_NAME = "服务时间明细超过十次服务中报表_%s.xlsx";
    private final static String REPORT_NAME = "服务时间明细超过十次服务中报表";

    private static String[] REPORT_HEADER =
        new String[]{"月份", "日期", "商店编码", "商店名称", "Market", "Banner", "城市", "服务商", "ISA编号",
            "ISA姓名", "ISA登录名", "ISA类型", "ISA分组",
            " 服务点代码", "当日总提交次数", "当日定位异常提交次数", "当日服务小时", "服务总时间", "结算上限小时", "实际结算小时"};

    private final static String SQL =
        "select to_char(schedule_date, 'YYYY/MM') as schedule_month,\n"
            + "       schedule_date,\n"
            + "       isa_all_info.store_code,\n"
            + "       isa_all_info.store_name,\n"
            + "       isa_all_info.seo_market,\n"
            + "       isa_all_info.banner,\n"
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
            + "       approval_status\n"
            + "from schedule.ga_store_check_report\n"
            + "         INNER JOIN bizprocess.isa_all_info\n"
            + "                    on  schedule.ga_store_check_report.provider_no = isa_all_info.provider_no and\n"
            + "                        schedule.ga_store_check_report.store_code = isa_all_info.store_code\n"
            + "where  schedule.ga_store_check_report.provider_no in (select provider_no\n"
            + "from (select distinct count(*)as count,provider_no\n"
            + "from  schedule.ga_store_check_report where check_status =2 group by service_time_group_id,provider_no) as a where count>=10)\n"
            + "and schedule_date >= ? and schedule_date <= ? %s\n"
            + "order by  schedule.ga_store_check_report.provider_no, check_time;\n";

    private final static String COUNT_SQL =
        "select service_time_group_id,provider_no,count(*)as count\n"
            + "       from schedule.ga_store_check_report where check_status =2 group by service_time_group_id,provider_no order by count desc limit 1";

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
            List<CheckDetailReportMoudle> list =
                DbUtils.querySql(sqlStr, CheckDetailReportMoudle.class, sqlParams.toArray());

            // 获取最大服务中次数
            List<CheckInServiceMaxMoudle> integers =
                DbUtils.querySql(COUNT_SQL, CheckInServiceMaxMoudle.class);
            IN_SERVICE_MAX_INDEX = integers.get(0).getCount();

            List<List<Object>> rows = new ArrayList();

            Map<Long, List<CheckDetailReportMoudle>> groupByScheduleId =
                list.stream().collect(Collectors.groupingBy(
                    CheckDetailReportMoudle::getSchedule_id));

            for (List<CheckDetailReportMoudle> values : groupByScheduleId.values()) {
                Map<String, List<CheckDetailReportMoudle>> groupByStoreCode =
                    values.stream().collect(Collectors.groupingBy(
                        CheckDetailReportMoudle::getStore_code));

                for (List<CheckDetailReportMoudle> valuesStore : groupByStoreCode.values()) {
                    if (!CollectionUtils.isEmpty(valuesStore)) {

                        int totalCheckNum = 0;
                        int totalAbnormalLocalCheckNum = 0;
                        //当日服务分钟
                        int serviceOnDay = 0;
                        //服务总时间分钟
                        int totalServiceTime = 0;

                        List<Object> row = new ArrayList();
                        CheckDetailReportMoudle checkDetailReportMoudle = valuesStore.get(0);
                        row.add(checkDetailReportMoudle.getSchedule_month());
                        row.add(checkDetailReportMoudle.getSchedule_date());
                        row.add(checkDetailReportMoudle.getStore_code());
                        row.add(checkDetailReportMoudle.getStore_name()== null ? "" : checkDetailReportMoudle.getStore_name());
                        row.add(checkDetailReportMoudle.getSeo_market()== null ? "" : checkDetailReportMoudle.getSeo_market());
                        row.add(checkDetailReportMoudle.getBanner()== null ? "" : checkDetailReportMoudle.getBanner());
                        row.add(checkDetailReportMoudle.getCity()== null ? "" : checkDetailReportMoudle.getCity());
                        //服务商
                        row.add(checkDetailReportMoudle.getSupplier()== null ? "" : checkDetailReportMoudle.getSupplier());
                        row.add(checkDetailReportMoudle.getProvider_id()== null ? "" : checkDetailReportMoudle.getProvider_id());
                        row.add(checkDetailReportMoudle.getProvider_name()== null ? "" : checkDetailReportMoudle.getProvider_name());
                        row.add(checkDetailReportMoudle.getProvider_no()== null ? "" : checkDetailReportMoudle.getProvider_no());
                        row.add(checkDetailReportMoudle.getProvider_type() == null ? "" : checkDetailReportMoudle.getProvider_type());
                        row.add(checkDetailReportMoudle.getProvider_role() == null ? "" : checkDetailReportMoudle.getProvider_role());
                        //服务点代码
                        row.add(checkDetailReportMoudle.getService_point_code() == null ? "" : checkDetailReportMoudle.getService_point_code());
                        row.add("");
                        row.add("");
                        row.add("");
                        row.add("");
                        row.add("");
                        row.add("");

                        //按照详细段分组
                        Map<Long, List<CheckDetailReportMoudle>> groupByServiceTimeGroupId =
                            valuesStore.stream().collect(Collectors.groupingBy(
                                CheckDetailReportMoudle::getService_time_group_id));
                        Map<Long, List<CheckDetailReportMoudle>> groupByServiceTimeGroupIdSort = new TreeMap<>(
                            Long::compareTo);
                        groupByServiceTimeGroupId
                            .forEach(groupByServiceTimeGroupIdSort::put);

                        int i1 = 0;
                        for (List<CheckDetailReportMoudle> valuesServiceTimeGroupId : groupByServiceTimeGroupIdSort
                            .values()) {
                            i1 = i1 + 1;

                            //开始服务
                            List<CheckDetailReportMoudle> startService = valuesServiceTimeGroupId
                                .stream()
                                .filter(o -> o.getCheck_status() == 1).collect(Collectors.toList());

                            //服务中
                            List<CheckDetailReportMoudle> inService = valuesServiceTimeGroupId
                                .stream()
                                .filter(o -> o.getCheck_status() == 2).collect(Collectors.toList());

                            //结束服务
                            List<CheckDetailReportMoudle> endService = valuesServiceTimeGroupId
                                .stream()
                                .filter(o -> o.getCheck_status() == 3).collect(Collectors.toList());

                            if (!CollectionUtils.isEmpty(startService)) {
                                switch (startService.get(0).getCheck_type()) {
                                    case 1:
                                        row.add("销售服务");
                                        break;
                                    case 2:
                                        row.add("课堂培训");
                                        break;
                                    case 3:
                                        row.add("盘点");
                                        break;
                                    default:
                                        row.add("未知");
                                        break;
                                }
                                row.add(startService.get(0).getCheck_time());
                            } else {
                                row.add("");
                                row.add("");
                            }

                            for (CheckDetailReportMoudle inServiceMoudel : inService) {
                                row.add(inServiceMoudel.getCheck_time());
                            }
                            for (int i = 0; i < IN_SERVICE_MAX_INDEX - inService.size(); i++) {
                                row.add("");
                            }

                            if (!CollectionUtils.isEmpty(endService)) {
                                row.add(endService.get(0).getCheck_time());
                            } else {
                                row.add("");
                            }

                            // 有效打卡次数
                            int effectiveCheckTime = 0;
                            //是否超过6小时
                            boolean overSixHour = false;
                            //有效服务时常
                            int effectiveServiceDuration = 0;
                            boolean lastIsEffective = true;
                            for (CheckDetailReportMoudle detailReportMoudle : valuesServiceTimeGroupId) {
                                // 判断是否异常打卡
                                if (detailReportMoudle.getIs_location_abnormal()
                                    || detailReportMoudle.getDuration() > 360
                                    || (detailReportMoudle.getApproval_status() != 3 && detailReportMoudle.getApproval_status()!=1)) {
                                    if(detailReportMoudle.getDuration() > 360){
                                        if (!overSixHour) {
                                            overSixHour = true;
                                        }
                                    }
                                    lastIsEffective = false;
                                } else {
                                    effectiveCheckTime = effectiveCheckTime + 1;
                                    if (lastIsEffective || (!lastIsEffective && detailReportMoudle.getDuration() < 360)) {
                                        effectiveServiceDuration =
                                            effectiveServiceDuration + detailReportMoudle
                                                .getDuration();

                                        totalServiceTime =
                                            totalServiceTime + detailReportMoudle.getDuration();
                                    }

                                    if((lastIsEffective || (!lastIsEffective && detailReportMoudle.getDuration() < 360)) && detailReportMoudle.getDuration() > 120){
                                        serviceOnDay =
                                            serviceOnDay + detailReportMoudle.getDuration();
                                    }
                                    lastIsEffective = true;
                                }

                                totalCheckNum = totalCheckNum + 1;

                                if (detailReportMoudle.getIs_location_abnormal()) {
                                    totalAbnormalLocalCheckNum = totalAbnormalLocalCheckNum + 1;
                                }
                            }
                            row.add(df.format((float) effectiveServiceDuration / 60));
                            if (overSixHour) {
                                row.add(1);
                            } else {
                                row.add("");
                            }
                            row.add(effectiveCheckTime);
                        }

                        if (i1 > LOOP_NUM) {
                            LOOP_NUM = i1;
                        }

                        row.set(14, totalCheckNum);
                        row.set(15, totalAbnormalLocalCheckNum);
                        row.set(16, df.format((float) totalServiceTime / 60));
                        row.set(17, df.format((float) totalServiceTime / 60));
                        row.set(18, 14);
                        row.set(19, serviceOnDay > 14*60 ? df.format((float) 14) :  df.format((float) serviceOnDay / 60));
                        rows.add(row);
                    }
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

    private void exportExcel(List<List<Object>> rows, String excelFile) {
        // 创建写excel实例
        ExcelWriter excelWriter = new ExcelWriter();
        String file = StringUtils.join(PathUtils.getAppPath(), excelFile);
        try {
            // 创建excel
            excelWriter.openExcel(new File(file));
            // 创建sheet
            excelWriter.createSheet(REPORT_NAME);

            List<String> strings = Arrays.asList(REPORT_HEADER);
            List<String> arrList = new ArrayList(strings);

            for (int i = 1; i <= LOOP_NUM; i++) {

                arrList.add("服务类型" + i);
                arrList.add("开始服务" + i);

                for (int inServiceIndex = 1; inServiceIndex <= IN_SERVICE_MAX_INDEX;
                    inServiceIndex++) {
                    arrList.add("服务中" + inServiceIndex);
                }

                arrList.add("结束服务" + i);
                arrList.add("服务时间" + i);
                arrList.add("是否超过6小时" + i);
                arrList.add("有效打卡次数" + i);
            }

            REPORT_HEADER = arrList.toArray(new String[arrList.size()]);

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
