package com.process.archivalservice.service;

import com.process.archivalservice.dao.ArchivalDao;
import com.process.archivalservice.model.ConfigType;
import com.process.archivalservice.model.Configuration;
import com.process.archivalservice.processor.DeletionProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DeletionService {

    @Autowired
    @Qualifier("archivalDao")
    ArchivalDao archivalDao;

    @Autowired
    DeletionProcessor processor;

    /***
     * This is the method which runs every specified minute.
     * It Check if there is any data eligible for deletion from archive tables.
     */
    @Scheduled(fixedDelayString = "${delete.delay.interval}")
    public void deleteData() {
        log.info("Starting Deletion Process");
        List<Configuration> configurations = archivalDao.getAllConfiguration(ConfigType.DELETION);
        if(configurations.isEmpty()) {
            log.info("No configurations found for deletion in the system.");
            return;
        } else {
            log.info("Total policies found for deletion are {}", configurations.size());
        }

        log.info("Fetching different geo locations at which data needs to be deleted.");
        List<String> locations = archivalDao.getGeoLocations();
        if(locations.isEmpty()) {
            log.info("No geo location configured for deletion.");
            return;
        } else {
            log.info("Total geo locations found are: {}", locations.size());
            log.info("{}", locations);
        }

        for (Configuration configuration : configurations) {
            for(String location : locations) {
                try {
                    processor.deleteData(configuration, location);
                } catch(Exception e) {
                    log.error("Deletion did not completed successfully for table: {}", configuration.getTableName());
                    log.error("{}", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        log.info("Deletion Process Completed");
    }
}
