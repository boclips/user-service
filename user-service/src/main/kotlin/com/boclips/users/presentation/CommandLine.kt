package com.boclips.users.presentation

import com.boclips.users.application.SynchronisationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class CommandLine(
    val env: Environment,
    val synchronisationService: SynchronisationService
) {
    @Autowired
    lateinit var app: ApplicationContext

    @PostConstruct
    fun onBoot() {
        when (env.getProperty("mode")) {
            "sync-hubspot-contacts" -> {
                synchronisationService.synchroniseCrmProfiles()
                System.exit(SpringApplication.exit(app))
            }

            "pull-users-from-keycloak" -> {
                synchronisationService.synchroniseUserAccounts()
                System.exit(SpringApplication.exit(app))
            }

            "sync-users-organisations" -> {
                synchronisationService.synchroniseUsersOrganisations()
                System.exit(SpringApplication.exit(app))
            }
        }
    }
}
