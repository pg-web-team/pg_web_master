package com.example.demo;

import com.example.demo.service.IsaScheduleReport;
import com.example.demo.utils.DbUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

@SpringBootApplication
public class IsaScheduleReportApplication {

    private static final String CMD_LINE_SYNTAX = "java -jar IsaScheduleReport.jar [args...]";
    private static final String CMD_LINE_EXAMPLE =
        "使用示例：\rjava -jar IsaScheduleReport.jar -c 80012345 -s \"2019-09-19 22:22:22\" -e \"2019-09-29 22:22:22\" -n sso -env qa/prod";

    public static void main(String[] args) {
        Map<String, String> params = parseCmdLine(args);
        if (params != null) {
            DbUtils.init(params);
            new IsaScheduleReport().export(params);
        }
    }

    private static Map<String, String> parseCmdLine(String[] args) {
        Map<String, String> paramMap = new HashMap<>();
        // 根据命令行参数定义Option对象，命令行参数名缩写、参数名全称、是否有参数值、参数描述
        Option opt0 = new Option("h", "help", false, "参数说明");
        // 设置该参数是否是必须的
        opt0.setRequired(false);

        Option opt1 = new Option("s", "startDate", true, "*开始时间 yyyy-MM-dd HH:mm:ss");
        opt1.setRequired(true);

        Option opt2 = new Option("e", "endDate", true, "*结束时间 yyyy-MM-dd HH:mm:ss");
        opt2.setRequired(true);

        Option opt3 = new Option("c", "storeCode", true, "店铺Code");
        opt3.setRequired(false);

        Option opt4 = new Option("n", "providerNo", true, "用户 SSO ID");
        opt4.setRequired(false);

        Option opt5 = new Option("env", "env", true, "环境qa/prod");
        opt5.setRequired(true);

        Options options = new Options();
        options.addOption(opt0);
        options.addOption(opt1);
        options.addOption(opt2);
        options.addOption(opt3);
        options.addOption(opt4);
        options.addOption(opt5);

        CommandLine cli = null;
        CommandLineParser cliParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();

        try {
            cli = cliParser.parse(options, args);
            // 根据不同参数执行不同逻辑
            if (cli.hasOption("h")) {
                helpFormatter.printHelp(CMD_LINE_SYNTAX, options);
                helpFormatter.printHelp(150, CMD_LINE_SYNTAX, "", options, CMD_LINE_EXAMPLE);
                return null;
            }
            if (cli.hasOption("s")) {
                paramMap.put("s", cli.getOptionValue("s"));
            }
            if (cli.hasOption("e")) {
                paramMap.put("e", cli.getOptionValue("e"));
            }
            if (cli.hasOption("c")) {
                paramMap.put("c", cli.getOptionValue("c"));
            }

            if (cli.hasOption("n")) {
                paramMap.put("n", cli.getOptionValue("n"));
            }

            if (cli.hasOption("env")) {
                paramMap.put("env", cli.getOptionValue("env"));
            }

        } catch (ParseException e) {
            // 解析失败用 HelpFormatter 打印 帮助信息 -s "2019-09-19 22:22:22" -c 800123456
            helpFormatter.printHelp(150, CMD_LINE_SYNTAX, "", options, CMD_LINE_EXAMPLE);
            return null;
        }
        return paramMap;
    }

}
