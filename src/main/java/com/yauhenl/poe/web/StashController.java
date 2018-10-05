package com.yauhenl.poe.web;

import com.yauhenl.poe.service.StashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StashController {

    @Autowired
    private StashService stashService;

    @GetMapping("/")
    public void getStashItems() {
        stashService.getStashes();
    }
}
