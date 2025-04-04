package com.process.archivalservice.processor;

import com.process.archivalservice.dao.ArchivalDao;
import com.process.archivalservice.model.Configuration;
import com.process.archivalservice.model.Row;
import com.process.archivalservice.util.ArchiveUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Component
public class DeletionProcessor {

    @Autowired
    @Qualifier("archivalDao")
    ArchivalDao archivalDao;

    /***
     * Responsible for identifying teh eligible daat for deletion. If data found then it deletes the data.
     * @param config has parameters define for evaluating the rows eligible for deletion from archive table.
     * @param location location at which the data needs to be deleted.
     */
    public void deleteData(Configuration config, String location) {
        log.info("Checking the eligible data for deletion from [ {} ] table at {} location", config.getTableName(), location);
        Timestamp latestTimestamp = ArchiveUtils.getLastTimestamp(config);
        List<Row> rowsToDeleted = archivalDao.getEligibleRowsForDeletion(config.getTableName(), location, latestTimestamp);
        if(rowsToDeleted.isEmpty()) {
            log.info("No eligible data found for deletion for [ {} ] table in {} location", config.getTableName(), location);
        } else {
            log.info("{} rows are eligible for deletion from table [ {} ] at {} location", rowsToDeleted.size(), config.getTableName(), location);
            deleteFromArchive(rowsToDeleted, config.getTableName(), location);
        }
    }


    /***
     * Responsible for deleting the data from a given table at a given location.
      * @param rowsToBeDeleted actual data which needs to be deleted.
     * @param table actual table from which the data needs to be deleted.
     * @param location location at which the data needs to be deleted.
     */
    private void deleteFromArchive(List<Row> rowsToBeDeleted, String table, String location) {
        try {
            log.info("Deleting the data from archive table [ {} ] at location {}", table, location);
            archivalDao.deleteData(rowsToBeDeleted, table, location);
            log.info("Data successfully deleted from archive table [ {} ] at location {}", table, location);
        } catch (Exception e) {
            log.error("Error Deleting the data from archive table [ {} ] at location {}", table, location);
            log.error("{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
