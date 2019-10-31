/**
 *
 */
package com.example.demo.service;

import com.example.demo.utils.DateUtils;
import com.example.demo.utils.DateUtils.TimeFormat;
import java.io.File;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Guotong
 *
 */
public interface Report {

    public String export(Map<String, String> params);

    public default String getExcelFile(String reportName, Map<String, String> params) {
        String outputFile = params.get("o");
        if (StringUtils.isNotBlank(outputFile)) {
            if (!StringUtils.endsWithIgnoreCase(outputFile, ".xls")
                || !StringUtils.endsWithIgnoreCase(outputFile, ".xlsx")) {
                outputFile = StringUtils.join(outputFile, ".xlsx");
            }
        } else {
            String dateTimeStr = DateUtils.parseTime(new Date(),
                TimeFormat.LONG_DATE_PATTERN_WITH_MILSEC_NONE_SPACE);
            outputFile = String.format(reportName, dateTimeStr);
            int index = 1;
            while (new File(outputFile).exists()) {
                outputFile = String.format(reportName, StringUtils.join(dateTimeStr, index++));
            }
        }
        return outputFile;
    }
}
