package com.process.archivalservice.service;

import com.process.archivalservice.dao.ArchivalDao;
import com.process.archivalservice.model.ConfigType;
import com.process.archivalservice.model.Configuration;
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
        log.info("Total configs found for archival are {}", configurations.size());
        for (Configuration configuration : configurations) {
            try {
                processor.archive(configuration);
            } catch(Exception e) {
                log.error("Archival did not completed successfully for table: {}", configuration.getTableName());
                log.error("{}", e.getMessage());
                e.printStackTrace();
            }
        }
        log.info("Archival Process Completed");
    }
}
