package com.process.archivalservice.service;

import com.process.archivalservice.dao.ArchivalDao;
import com.process.archivalservice.model.ConfigType;
import com.process.archivalservice.model.Configuration;
import com.process.archivalservice.processor.ArchivalProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class ArchivalService {

    @Autowired
    @Qualifier("archivalDao")
    ArchivalDao archivalDao;

    @Autowired
    ArchivalProcessor processor;

    /***
     * This is the method which runs every specified minute.
     * It Check if there is any data eligible for archival from main table
     */
    @Scheduled(fixedDelayString = "${archival.delay.interval}")
    public void archiveData() {
        log.info("Starting Archival Process");

        List<Configuration> configurations = archivalDao.getAllConfiguration(ConfigType.ARCHIVAL);
        if(configurations.isEmpty()) {
            log.info("No archival configurations found in the system.");
            return;
        } else {
            log.info("Total policies found for archival are {}", configurations.size());
        }

        log.info("Fetching different geo locations at which data needs to be archived.");
        List<String> locations = archivalDao.getGeoLocations();
        if(locations.isEmpty()) {
            log.info("No geo location configured for archival.");
            return;
        } else {
            log.info("Total archival geo locations found are: {}", locations.size());
            log.info("{}", locations);
        }
        for (Configuration configuration : configurations) {
            try {
                processor.archive(configuration, locations);
            } catch(Exception e) {
                log.error("Archival did not completed successfully for table: {}", configuration.getTableName());
                log.error("{}", e.getMessage());
                e.printStackTrace();
            }
        }
        log.info("Archival Process Completed");
    }
}
