package com.yauhenl.poe.web

import com.yauhenl.poe.service.StashService
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController {

    @Autowired
    private val stashService: StashService? = null

    @GetMapping("/")
    fun get() : List<Document>? {
        return stashService?.find()
    }
}