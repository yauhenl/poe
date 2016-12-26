package com.yauhenl.poe.service.job;

import com.yauhenl.poe.domain.JobStatus;
import com.yauhenl.poe.service.StashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class UpdateStashesJob {
    private static final Logger logger = LoggerFactory.getLogger(UpdateStashesJob.class);

    @Autowired
    private StashService stashService;

    @Scheduled(fixedRate = 5000)
    public void updateStashes() throws InterruptedException, ExecutionException {
        Future<JobStatus> result = stashService.updateStashes();
        JobStatus status = result.get();
        logger.info(status.name());
    }
}
