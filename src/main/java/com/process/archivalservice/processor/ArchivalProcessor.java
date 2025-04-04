package com.process.archivalservice.processor;

import com.process.archivalservice.dao.ArchivalDao;
import com.process.archivalservice.model.Configuration;
import com.process.archivalservice.model.Row;
import com.process.archivalservice.util.ArchiveUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Component
public class ArchivalProcessor {

    @Autowired
    @Qualifier("archivalDao")
    ArchivalDao archivalDao;

    /***
     * responsiblef for archiving the data. This consist of 2 step process. First the eligoble
     * data has been copied into archive table. If any of the archive copy fails, then it rollbacks all the transactions.
     * If All the archive copy is successfull, then it deletes the data from the main table as well.
     * @param config configuration porams which is needed to see the eligible rows for the table.
     * @param geoLocations list of all geo location at which data needs to be archived.
     */
    @Transactional(rollbackFor = Exception.class)
    public void archive(Configuration config, List<String> geoLocations) {
        log.info("Checking the eligible data for archival from [ {} ] table", config.getTableName());
        Timestamp latestTimestamp = ArchiveUtils.getLastTimestamp(config);
        List<Row> rowsToBeArchived = archivalDao.getEligibleRows(config.getTableName(), "main", latestTimestamp);
        if(rowsToBeArchived.isEmpty()) {
            log.info("No eligible data found for archival in [ {} ] table", config.getTableName());
        } else {
            log.info("{} rows are eligible for archival in table [ {} ]", rowsToBeArchived.size(), config.getTableName());
            for(String location: geoLocations) {
                archiveFromMain(rowsToBeArchived, config.getTableName(), location);
            }
            deleteFromMain(rowsToBeArchived, config.getTableName());
        }
    }

    /***
     * Responsible for archiving the given set of data and copy into archive table for a given geo location.
     * @param rowsToBeArchived data which needs to be archived.
     * @param table table at which at needs to be archived.
     * @param location location at which data archival is performed.
     */
    private void archiveFromMain(List<Row> rowsToBeArchived, String table, String location) {
        try{
            log.info("Archiving the data of [ {} ] table into {} geo location.", table, location.toUpperCase());
            archivalDao.archiveData(rowsToBeArchived, table, location);
            log.info("Data Archived successfully for table [ {} ] into {} geo location.", table, location.toUpperCase());
        } catch(Exception e) {
            log.error("Error archiving the data for [ {} ] table into {} geo location.", table, location.toUpperCase(), e);
            throw new RuntimeException("Not able to archive the data from [ " + table + " ] table");
        }
    }

    /***
     * It is responsible for deleting the given set of data from the specified main table.
     * @param rowsToBeDeleted data which needs to be deleted.
     * @param table table at which it needs to be deleted.
     */
    private void deleteFromMain(List<Row> rowsToBeDeleted, String table) {
        try {
            log.info("Deleting the data from main table: [ {} ]", table);
            archivalDao.deleteData(rowsToBeDeleted, table, "main");
            log.info("Data successfully deleted from main table [ {} ]", table);
        } catch (Exception e) {
            log.error("Error Deleting the data from main table: [ {} ]", table);
            log.error("{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
