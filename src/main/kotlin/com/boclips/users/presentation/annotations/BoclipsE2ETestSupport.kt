package com.boclips.users.presentation.annotations

import org.springframework.context.annotation.Profile

@Profile("testing", "test")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class BoclipsE2ETestSupport
