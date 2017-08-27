package com.yauhenl.poe.service.job;

import com.yauhenl.poe.domain.JobStatus;
import com.yauhenl.poe.service.PublicStashTabsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class ApiDataJob {
    private static final Logger logger = LoggerFactory.getLogger(ApiDataJob.class);

    @Autowired
    private PublicStashTabsService publicStashTabsService;

    @Scheduled(fixedRate = 5000)
    public void updatePublicStashTabs() throws ExecutionException, InterruptedException {
        Future<JobStatus> result = publicStashTabsService.writeNext();
        JobStatus status = result.get();
        logger.info(status.name());
    }
}
