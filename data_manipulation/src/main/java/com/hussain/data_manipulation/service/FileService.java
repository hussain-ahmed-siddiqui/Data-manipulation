package com.hussain.data_manipulation.service;

import com.hussain.data_manipulation.model.entity.ExcelCellData;
import com.hussain.data_manipulation.model.entity.ExcelRowData;
import com.hussain.data_manipulation.util.FileUtil;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class FileService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    GridFsTemplate gridFsTemplate;
    public String extractAndSaveColumnData(MultipartFile dataFile) throws Exception {
        CompletableFuture<String> hashThread = getFileHash(dataFile.getInputStream(), "MD5");
        try(InputStream is = dataFile.getInputStream();
            ReadableWorkbook workbook = new ReadableWorkbook(is)){
            Sheet sheet = workbook.getFirstSheet();
            Row headerRow = sheet.openStream().findFirst().orElseThrow(()->
                    new RuntimeException("Missing header row"));

            Map<String, Integer> columnIndexMap = new HashMap<>();
            List<Cell> headerCells = headerRow.getCells(0, headerRow.getCellCount());
            for(Cell headerCell: headerCells){
                columnIndexMap.put(headerCell.getText(), headerCell.getColumnIndex());
            }

            List<ExcelRowData> rowDataList = new ArrayList<>();

            sheet.openStream().skip(1).forEach(row -> {
                ExcelRowData rowData = new ExcelRowData();
                for (Map.Entry<String, Integer> entry : columnIndexMap.entrySet()) {
                    String cellValue = row.getCellText(entry.getValue());
                    if (cellValue == null || cellValue.isEmpty()) continue;
                    ExcelCellData cellData = new ExcelCellData(entry.getKey(), cellValue);
                    rowData.getRow().add(cellData);
                }
                rowDataList.add(rowData);
            });
            String fileHash = hashThread.join();
            GridFSFile existingFile = gridFsTemplate.findOne(new Query(
                    Criteria.where("metadata.hash").is(fileHash)
            ));
            if(existingFile != null) return "{ \"status\" : \"FILE ALREADY EXISTS\" }";
            gridFsTemplate.store(dataFile.getInputStream(), dataFile.getOriginalFilename(), dataFile.getContentType(),
                    new Document("hash", fileHash));
            // Save in bulk after processing all rows
            mongoTemplate.insertAll(rowDataList);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return "{ \"status\" : \"FAILURE\" }";
        }
        return "{ \"status\" : \"SUCCESS\" }";
    }
    @Async
    public static CompletableFuture<String> getFileHash(InputStream inputStream, String algorithm) throws Exception {
        return CompletableFuture.completedFuture(FileUtil.computeHash(inputStream, algorithm));
    }
}
