package com.boclips.users

import com.boclips.events.spring.EnableBoclipsEvents
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableRetry
@EnableCaching
@EnableScheduling
@EnableBoclipsEvents(appName = "user-service")
class UserServiceApplication

fun main(args: Array<String>) {
    runApplication<UserServiceApplication>(*args)
}
