package com.example.demo.utils.excel;

import org.apache.poi.ss.usermodel.Row;

public interface RowCallback {
	
	void callback(Row row, WorkbookObject workBook);
	
}
