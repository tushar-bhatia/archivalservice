package com.process.archivalservice.service;

import com.process.archivalservice.dao.ArchivalDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeletionService {

    @Autowired
    @Qualifier("archivalDao")
    ArchivalDao archivalDao;

    /***
     * This is the method which runs every specified minute.
     * It Check if there is any data eligible for deletion from archive tables.
     */
    @Scheduled(fixedDelayString = "${delete.delay.interval}")
    public void deleteData() {
        /*log.info("Starting Deletion Process");

        log.info("Deletion Process Completed");*/
    }
}
