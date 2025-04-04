package com.process.archivalservice.controller;

import com.process.archivalservice.dao.ArchivalDao;
import com.process.archivalservice.model.Row;
import com.process.archivalservice.util.ArchiveUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping({"/{table}/{location}", "/{table}"})
    public List<Map<String, Object>> viewArchiveData(
            @NotBlank(message = "Table name can't be blank")
            @NotEmpty(message = "Table name can't be empty")
            @NotNull(message = "Table name can't be null")
            @Pattern(regexp = "student|grades|attendance", message = "table name should either be student or grades or attendance")
            @PathVariable(name = "table") String tableName,
            @Pattern(regexp = "us|ln|hk", message = "location should either be hk or ln or us")
            @PathVariable(name="location", required = false) String location) {
        log.info("Request Received to read the data from [ {} ] archive table.", tableName);
        if(location == null) location = "us";
        List<Row> rows = archivalDao.getDataFromTable(tableName, location);
        return ArchiveUtils.parseRows(rows);
    }
}
