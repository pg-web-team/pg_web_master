package com.example.demo.service;

import com.example.demo.domain.IsaScheduleReportMoudle;
import com.example.demo.domain.ServiceTypeCode;
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
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.util.CollectionUtils;

/**
 * @author Guotong
 */

public class IsaScheduleReport implements Report {

    private static int LOOP_NUM = 0;

    private final static String REPORT_FILE_NAME = "ISA服务计划表_%s.xlsx";
    private final static String REPORT_NAME = "ISA服务计划表";

    private static String[] REPORT_HEADER =
        new String[]{"月份", "区域", "城市", "服务商", "商店", "SPOC 名称", "SPOC联系方式", "ISA编号", "ISA名称",
            "服务点代码", "ISA分组", "联系方式", "结束服务日期",
            "调店日期", "休假类型", "事假时长", "当天总服务计划小时数", "服务计划日期", "午餐时间", "晚餐时间"};

    private final static String SQL = "select ga_schedule.schedule_month,\n"
        + "       isa_all_info.province,\n"
        + "       isa_all_info.city,\n"
        + "       isa_all_info.supplier,\n"
        + "       isa_all_info.store_name,\n"
        + "       isa_all_info.provider_id,\n"
        + "       isa_all_info.provider_name,\n"
        + "       isa_all_info.provider_role,\n"
        + "       isa_all_info.provider_type,\n"
        + "       isa_all_info.service_end_date,\n"
        + "       isa_all_info.service_point_code,\n"
        + "       isa_all_info.store_owner_name,\n"
        + "       isa_all_info.store_owner_phone,\n"
        + "       isa_all_info.transfer_date,\n"
        + "       isa_all_info.contact,\n"
        + "       ga_schedule_detail.schedule_id,\n"
        + "       ga_schedule.schedule_date,\n"
        + "       ga_schedule.service_status,\n"
        + "       ga_schedule_detail.service_type,\n"
        + "       ga_schedule_detail.start_time,\n"
        + "       ga_schedule_detail.end_time,\n"
        + "       ga_schedule_detail.service_duration\n"
        + "from schedule.ga_schedule\n"
        + "         left join schedule.ga_schedule_detail on ga_schedule.id = ga_schedule_detail.schedule_id\n"
        + "         INNER JOIN bizprocess.isa_all_info\n"
        + "                    on ga_schedule_detail.provider_no = isa_all_info.provider_no and\n"
        + "                       ga_schedule_detail.store_code = isa_all_info.store_code\n"
        + "where service_status is not null and schedule_date >= ? and schedule_date <= ? %s\n";

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
            whereStr.append(" AND ga_schedule_detail.store_code = ?");
            sqlParams.add(storeCode);
        }
        if (StringUtils.isNotBlank(providerNo)) {
            whereStr.append(" AND ga_schedule_detail.provider_no = ?");
            sqlParams.add(providerNo);

        }
        String sqlStr = String.format(SQL, whereStr);

        try {
            List<IsaScheduleReportMoudle> list =
                DbUtils.querySql(sqlStr, IsaScheduleReportMoudle.class, sqlParams.toArray());

            List<List<Object>> rows = new ArrayList();

            Map<Long, List<IsaScheduleReportMoudle>> groupByScheduleId =
                list.stream().collect(Collectors.groupingBy(
                    IsaScheduleReportMoudle::getSchedule_id));

            for (List<IsaScheduleReportMoudle> values : groupByScheduleId.values()) {
                Map<String, List<IsaScheduleReportMoudle>> groupByStoreCode =
                    values.stream().collect(Collectors.groupingBy(
                        IsaScheduleReportMoudle::getStore_name));

                for (List<IsaScheduleReportMoudle> valuesStore : groupByStoreCode.values()) {
                    if (!CollectionUtils.isEmpty(valuesStore)) {
                        List<Object> row = new ArrayList();
                        IsaScheduleReportMoudle isaScheduleReportMoudle = valuesStore.get(0);
                        row.add(isaScheduleReportMoudle.getSchedule_month());
                        row.add(isaScheduleReportMoudle.getProvince());
                        row.add(isaScheduleReportMoudle.getCity());
                        row.add(isaScheduleReportMoudle.getSupplier());
                        row.add(isaScheduleReportMoudle.getStore_name());
                        // spoc 名称
                        row.add(isaScheduleReportMoudle.getStore_owner_name() == null ? ""
                            : isaScheduleReportMoudle.getStore_owner_name());
                        // spoc 联系方式
                        row.add(isaScheduleReportMoudle.getStore_owner_phone() == null ? ""
                            : isaScheduleReportMoudle.getStore_owner_phone());
                        row.add(isaScheduleReportMoudle.getProvider_id());
                        row.add(isaScheduleReportMoudle.getProvider_name());
                        // 服务点代码
                        row.add(isaScheduleReportMoudle.getService_point_code() == null ? ""
                            : isaScheduleReportMoudle.getService_point_code());
                        row.add(isaScheduleReportMoudle.getProvider_role() == null ? ""
                            : isaScheduleReportMoudle.getProvider_role());
                        row.add(isaScheduleReportMoudle.getContact() == null ? ""
                            : isaScheduleReportMoudle.getContact());

                        row.add(isaScheduleReportMoudle.getService_end_date() == null ? ""
                            : isaScheduleReportMoudle.getService_end_date());
                        //转岗日期
                        row.add(isaScheduleReportMoudle.getTransfer_date() == null ? ""
                            : isaScheduleReportMoudle.getTransfer_date());

                        //休假
                        List<IsaScheduleReportMoudle> vacations = valuesStore.stream()
                            .filter(o -> o.getService_type() > 6).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(vacations)) {
                            row.add(ServiceTypeCode.getValue(vacations.get(0).getService_type()));
                        } else {
                            row.add("");
                        }

                        int absenceVacationsSum = valuesStore.stream()
                            .filter(o -> o.getService_type() == 6)
                            .mapToInt(IsaScheduleReportMoudle::getService_duration).sum();

                        if (absenceVacationsSum == 0) {
                            row.add(df.format(0));
                        } else {
                            row.add(df.format(-(float) absenceVacationsSum / 60));
                        }

                        //总服务小时数
                        int serviceSummaryDuration = valuesStore.stream()
                            .mapToInt(IsaScheduleReportMoudle::getService_duration).sum();

                        row.add(df.format((float) serviceSummaryDuration / 60));

                        //服务日期
                        row.add(valuesStore.get(0).getSchedule_date());

                        //午餐时间
                        List<IsaScheduleReportMoudle> lunchs = valuesStore.stream()
                            .filter(o -> o.getService_type() == 4).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(lunchs)) {
                            row.add(
                                lunchs.get(0).getStart_time() + "-" + lunchs.get(0).getEnd_time());
                        } else {
                            row.add("");
                        }

                        //晚餐时间
                        List<IsaScheduleReportMoudle> dinners = valuesStore.stream()
                            .filter(o -> o.getService_type() == 5).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(dinners)) {
                            row.add(dinners.get(0).getStart_time() + "-" + dinners.get(0)
                                .getEnd_time());
                        } else {
                            row.add("");
                        }

                        List<IsaScheduleReportMoudle> services = valuesStore.stream()
                            .filter(o -> ServiceTypeCode.isServiceInclude(o.getService_type()))
                            .collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(services)) {

                            if (services.size() > LOOP_NUM) {
                                LOOP_NUM = services.size();
                            }

                            for (int i = 0; i < services.size(); i++) {
                                IsaScheduleReportMoudle isaScheduleReportMoudle1 = services.get(i);
                                row.add(ServiceTypeCode
                                    .getValue(isaScheduleReportMoudle1.getService_type()));
                                row.add(isaScheduleReportMoudle1.getStart_time());
                                row.add(isaScheduleReportMoudle1.getEnd_time());
                            }
                        } else {
                            row.add("");
                        }
                        rows.add(row);
                    }

                }
            }

            excelFile = getExcelFile(REPORT_FILE_NAME, params);
            exportExcel(rows, excelFile);

        } catch (SQLException e) {
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
                arrList.add("服务计划类型" + i);
                arrList.add("服务计划开始时间" + i);
                arrList.add("服务计划结束时间" + i);
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
