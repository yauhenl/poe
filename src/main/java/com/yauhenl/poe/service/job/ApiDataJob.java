package com.yauhenl.poe.service.job;

import com.yauhenl.poe.service.PublicStashTabsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class ApiDataJob {
    @Autowired
    private PublicStashTabsService publicStashTabsService;

    @Scheduled(fixedRate = 5000)
    public void updatePublicStashTabs() {
        publicStashTabsService.writeNext();
    }
}
