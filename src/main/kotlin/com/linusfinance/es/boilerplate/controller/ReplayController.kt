package com.linusfinance.es.boilerplate.controller

import com.linusfinance.es.boilerplate.replay.ReplayService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ReplayController(private val replayService: ReplayService) {

    @PostMapping("/replay")
    fun replay(): String {
        replayService.replay("com.linusfinance.es.boilerplate.query")
        return "Replay started"
    }
}