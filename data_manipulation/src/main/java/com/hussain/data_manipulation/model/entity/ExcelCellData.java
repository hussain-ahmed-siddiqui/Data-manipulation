package com.hussain.data_manipulation.model.entity;

import lombok.Data;

@Data
public class ExcelCellData {
    public ExcelCellData(String columnName, String cellText){
        this.columnId = columnName;
        this.cellValue = cellText;
    }
    String columnId;
    String cellValue;
}
