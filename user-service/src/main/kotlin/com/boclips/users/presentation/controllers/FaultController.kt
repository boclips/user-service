package com.boclips.users.presentation.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.ArrayList

@RestController
class FaultController {
    @GetMapping("/faults/memory-leak")
    fun memoryLeak() {
        val list = ArrayList<String>()
        repeat(1_000_000_000) {
            list.add("hello".repeat(it))
        }
    }
}