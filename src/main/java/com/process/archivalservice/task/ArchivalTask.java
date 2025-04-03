package com.process.archivalservice.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArchivalTask {

    @Autowired
    @Qualifier("databaseTemplate")
    JdbcTemplate databaseTemplate;

    /***
     * This is the method which runs every specified minute.
     * It Check if there is any data eligible for archival from main table
     */
    @Scheduled(fixedDelayString = "${archival.delay.interval}")
    public void archiveData() {
        log.info("Starting Archival Process");

        log.info("Archival Process Completed");
    }
}
