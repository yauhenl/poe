package com.yauhenl.poe.service.job;

import com.yauhenl.poe.domain.JobStatus;
import com.yauhenl.poe.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class ReindexProductsJob {
    private static final Logger logger = LoggerFactory.getLogger(ReindexProductsJob.class);

    @Autowired
    private SearchService searchService;

    @Scheduled(fixedRate = 10000)
    public void reindexProducts() throws IOException, ExecutionException, InterruptedException {
        Future<JobStatus> result = searchService.reindexProducts();
        JobStatus status = result.get();
        logger.info(status.name());
    }
}
