package com.hussain.data_manipulation.controller;

import com.hussain.data_manipulation.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/file")
public class FileController {
    @Autowired
    FileService fileService;

    @PostMapping(value = "/extract-excel-data-columns", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> extractDataColumnsByName(@RequestBody MultipartFile file) throws Exception {
        return ResponseEntity.ok().body(fileService.extractAndSaveColumnData(file));
    }
}
