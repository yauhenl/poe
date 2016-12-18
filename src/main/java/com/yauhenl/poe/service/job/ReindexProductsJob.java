package com.yauhenl.poe.service.job;

import com.yauhenl.poe.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ReindexProductsJob {

    @Autowired
    private SearchService searchService;

    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void reindexProducts() throws IOException {
        searchService.reindexProducts();
    }
}
