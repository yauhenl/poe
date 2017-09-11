package com.yauhenl.poe.service.job;

import com.yauhenl.poe.service.PublicStashTabsService;
import com.yauhenl.poe.service.StashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DataParserJob {

    @Autowired
    private StashService stashService;

    @Autowired
    private PublicStashTabsService publicStashTabsService;

    @Scheduled(fixedDelay = 5000)
    public void parseData() {
        int page = 0;
        int pageSize = 100;
        while ((page - 1) * page < publicStashTabsService.count()) {
            stashService.parseData(page, pageSize);
            page++;
        }
    }
}
