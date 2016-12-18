package com.yauhenl.poe.service.job;

import com.yauhenl.poe.service.StashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UpdateStashesJob {

    @Autowired
    private StashService stashService;

    @Scheduled(fixedRate = 5000)
    public void updateStashes() {
        stashService.updateStashes();
    }
}
