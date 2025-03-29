package com.hussain.data_manipulation.model.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "excel_rows")
public class ExcelRowData {
    List<ExcelCellData> row = new ArrayList<>();
}
