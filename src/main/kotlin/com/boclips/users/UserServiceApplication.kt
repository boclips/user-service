package com.boclips.users

import com.boclips.eventbus.EnableBoclipsEvents
import org.springframework.boot.actuate.autoconfigure.mongo.MongoHealthIndicatorAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(exclude = [MongoHealthIndicatorAutoConfiguration::class])
@EnableRetry
@EnableCaching
@EnableScheduling
@EnableBoclipsEvents
class UserServiceApplication

fun main(args: Array<String>) {
    runApplication<UserServiceApplication>(*args)
}
