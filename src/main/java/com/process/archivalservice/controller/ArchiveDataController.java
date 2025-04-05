package com.process.archivalservice.controller;

import com.process.archivalservice.dao.ArchivalDao;
import com.process.archivalservice.model.Row;
import com.process.archivalservice.util.ArchiveUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/archive")
public class ArchiveDataController {

    @Autowired
    @Qualifier("archivalDao")
    ArchivalDao archivalDao;

    @PreAuthorize( "hasRole('admin') or hasRole(#tableName.toLowerCase())")
    @GetMapping({"/{table}/{location}", "/{table}"})
    public ResponseEntity<?> viewArchiveData(
            @Valid
            @NotBlank(message = "Table name can't be blank")
            @NotEmpty(message = "Table name can't be empty")
            @NotNull(message = "Table name can't be null")
            @Pattern(regexp = "student|grades|attendance", message = "table name should either be student or grades or attendance")
            @PathVariable(name = "table") String tableName,
            @Pattern(regexp = "us|ln|hk", message = "location should either be hk or ln or us")
            @PathVariable(name="location", required = false) String location) {
        tableName = tableName.toLowerCase();
        log.info("Request Received to read the data from [ {} ] archive table.", tableName);
        location = location==null ? "us" : location.toLowerCase();
        List<Row> rows = archivalDao.getDataFromTable(tableName, location);
        Map<String, List<Map<String, Object>>> response = new HashMap<>(1);
        response.put(tableName, ArchiveUtils.parseRows(rows));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
