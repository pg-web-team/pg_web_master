package com.example.demo.utils.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

public class WorkbookObject {
	private Workbook workBook;

	WorkbookObject(Workbook workBook) {
		this.workBook = workBook;
	}

	public CellStyle createCellStyle() {
		return workBook.createCellStyle();
	}

	public Font createFont() {
		return workBook.createFont();
	}
}
