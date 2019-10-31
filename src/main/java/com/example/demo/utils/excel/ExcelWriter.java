package com.example.demo.utils.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EmptyFileException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {

    //private final static Logger logger = LoggerFactory.getLogger(ExcelWriter.class);

    private File file;
    private Workbook workBook = null;
    private WorkbookObject workBookObj;
    private Sheet sheet;
    //    private boolean isHSSF = false;
    private int rowNum = 0;

    public ExcelWriter() {

    }

    public ExcelWriter(File file) {
        this.initWorkBook(file);
    }

    private ExcelWriter initWorkBook(File file) {
        this.file = file;
        if (StringUtils.endsWithIgnoreCase(file.getName(), ".xlsx")) {
            workBook = new SXSSFWorkbook();
//            isHSSF = false;
        } else if (StringUtils.endsWithIgnoreCase(file.getName(), ".xls")) {
            workBook = new HSSFWorkbook();
//            isHSSF = true;
        }
        workBookObj = new WorkbookObject(workBook);
        return this;
    }

    public ExcelWriter openExcel(File file) throws Exception {
        this.file = file;
        try {
            workBook = WorkbookFactory.create(file);
            workBookObj = new WorkbookObject(workBook);
            return this;
        } catch (EmptyFileException | FileNotFoundException ex) {
            return this.initWorkBook(file);
        }
    }

    public ExcelWriter createSheet(String sheetName) {
        sheet = workBook.createSheet(sheetName);
        return this;
    }

    public ExcelWriter createSheet(List<String> sheetNameList) {
        for (String sheetName : sheetNameList) {
            sheet = workBook.createSheet(sheetName);
        }
        return this;
    }

    public ExcelWriter setColumnWidth(int column, int width) {
        sheet.setColumnWidth(column, width);
        return this;
    }

    public ExcelWriter setColumnWidth(int beginColumn, int endColumn, int width) {
        for (int i = beginColumn; i < endColumn; i++) {

            sheet.setColumnWidth(i, width);
        }
        return this;
    }

    public ExcelWriter autoSizeColumn(int column) {
        sheet.autoSizeColumn(column);
        return this;
    }

    public ExcelWriter autoSizeColumnByLen(int columnLen) {
        columnLen = columnLen < 0 ? 0 : columnLen;
        for (int i = 0; i < columnLen; i++) {
            sheet.autoSizeColumn(i);
        }
        return this;
    }

    public ExcelWriter setSheetHidden(String sheetName) {
//        workBook.setSheetHidden(workBook.getSheetIndex(sheetName), 2);
        workBook.setSheetHidden(workBook.getSheetIndex(sheetName), true);
        return this;
    }

    public ExcelWriter setColumnHidden(int column, boolean hidden) {
        sheet.setColumnHidden(column, hidden);
        return this;
    }

    public ExcelWriter setRow(int rowNum) {
        this.rowNum = rowNum;
        return this;
    }

    public ExcelWriter createRow(RowCallback rowCallback) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        rowNum++;
        rowCallback.callback(row, workBookObj);
        return this;
    }

    public ExcelWriter createRow(final int rowNumber, RowCallback rowCallback) {
        rowNum = rowNumber;

        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        rowNum++;
        rowCallback.callback(row, workBookObj);
        return this;
    }

    public ExcelWriter getSheet(String sheetName) {
        sheet = workBook.getSheet(sheetName);
        sheet = sheet == null ? workBook.createSheet(sheetName) : sheet;
        return this;
    }

    public ExcelWriter createFreezePane(int colSplit, int rowSplit, int leftmostColumn,
        int topRow) {
        sheet.createFreezePane(colSplit, rowSplit, leftmostColumn, topRow);
        return this;
    }

    public ExcelWriter createRow(String sheetName, RowCallback rowCallback) {
        sheet = workBook.getSheet(sheetName);
        this.createRow(rowCallback);
        return this;
    }

    public ExcelWriter addMergedRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
        try {
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
        } catch (Exception e) {
        }
        return this;
    }


    public ExcelWriter creatExcelName(String excelName) {

        return this;
    }

    public ExcelWriter closeQuietly() {
        FileOutputStream outputStream = null;
        File tmp = null;
        try {
            tmp = File.createTempFile(UUID.randomUUID().toString(), "xlsx");
            outputStream = new FileOutputStream(tmp);
            workBook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
        } finally {
            IOUtils.closeQuietly(outputStream, workBook);
        }
        try {
            FileUtils.copyFile(tmp, file);
        } catch (Exception e) {
        }
        file = null;
        workBook = null;
        sheet = null;
        return this;
    }

}
