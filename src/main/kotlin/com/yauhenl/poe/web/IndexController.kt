package com.yauhenl.poe.web

import com.yauhenl.poe.service.ApiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController {

    @Autowired
    private val apiService: ApiService? = null

    @GetMapping("/")
    fun get() {
        apiService?.getPublicStashTabs()
    }
}