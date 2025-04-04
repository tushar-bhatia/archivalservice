package com.process.archivalservice.service;

import com.process.archivalservice.dao.ArchivalDao;
import com.process.archivalservice.model.Configuration;
import com.process.archivalservice.model.Row;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class ArchivalProcessor {

    @Autowired
    @Qualifier("archivalDao")
    ArchivalDao archivalDao;

    @Transactional(rollbackFor = Exception.class)
    public void archive(Configuration config) {
        log.info("Checking the eligible data from {} table", config.getTableName());
        Timestamp latestTimestamp = getLastTimestamp(config);
        List<Row> rowsToBeArchived = archivalDao.getResults(config.getTableName(), latestTimestamp);
        if(rowsToBeArchived.isEmpty()) {
            log.info("No eligible row found in [ {} ] table", config.getTableName());
        } else {
            log.info("{} rows are eligible in table {}", rowsToBeArchived.size(), config.getTableName());
            List<String> geoLocations = archivalDao.getGeoLocations();
            for(String location: geoLocations) {
                archiveFromMain(rowsToBeArchived, config.getTableName(), location);
            }
            deleteFromMain(rowsToBeArchived, config.getTableName());
        }
    }

    private void archiveFromMain(List<Row> rowsToBeArchived, String table, String location) {
        try{
            log.info("Archiving the data of [ {} ] table into {} geo location.", table, location.toUpperCase());
            archivalDao.archiveData(rowsToBeArchived, table, location);
            log.info("Data Archived successfully for table [ {} ] into {} geo location.", table, location.toUpperCase());
        } catch(Exception e) {
            log.error("Error archiving the data for [ {} ] table into {} geo location.", table, location.toUpperCase(), e);
            throw new RuntimeException("Not able to archive the data");
        }
    }

    private void deleteFromMain(List<Row> rowsToBeArchived, String table) {
        try {
            log.info("Deleting the data from main table: [ {} ]", table);
            archivalDao.deleteData(rowsToBeArchived, table, "main");
            log.info("Data successfully deleted from main table [ {} ]", table);
        } catch (Exception e) {
            log.error("Error Deleting the data from main table: [ {} ]", table);
            log.error("{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Timestamp getLastTimestamp(Configuration config) {
        LocalDateTime now = LocalDateTime.now();
        now.minusYears(config.getYears());
        now.minusMonths(config.getMonths());
        now.minusWeeks(config.getWeeks());
        now.minusDays(config.getDays());
        now.minusHours(config.getHours());
        now.minusMinutes(config.getMinutes());
        return Timestamp.valueOf(now);
    }
}
