package com.boclips.users.presentation

import com.boclips.users.application.SynchronisationService
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import kotlin.system.exitProcess

@Component
class CommandLine(
    val env: Environment,
    val synchronisationService: SynchronisationService
) {
    @PostConstruct
    fun onBoot() {
        when (env.getProperty("mode")) {
            "sync-hubspot-contacts" -> {
                synchronisationService.synchroniseCrmProfiles()
                exitProcess(0)
            }

            "pull-users-from-keycloak" -> {
                synchronisationService.synchroniseUserAccounts()
                exitProcess(0)
            }
        }
    }
}
