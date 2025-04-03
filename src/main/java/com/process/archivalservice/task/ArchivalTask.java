package com.process.archivalservice.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArchivalTask {

    @Autowired
    @Qualifier("archiveTemplate")
    JdbcTemplate archiveTemplate;

    /***
     * This is the method which runs every specified minute.
     */
    @Scheduled(fixedDelayString = "${archival.delay.interval}")
    public void archiveData() {
        log.info("Starting Archival Process");

        log.info("Archival Process Completed");
    }
}
