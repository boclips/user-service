package com.boclips.users.application

import com.boclips.users.domain.service.UserService
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper.Companion.TEACHER_ROLE
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class UpdateTeachers(
    val userService: UserService,
    val keycloakWrapper: KeycloakWrapper
) {
    companion object : KLogging()

    operator fun invoke() {
        logger.info { "Start migration..." }
        userService.findAllUsers()
            .forEach { user ->
                if (keycloakWrapper.isInGroup(user.id.value, "teachers")) {
                    keycloakWrapper.removeFromGroup(user.id.value, "teachers")
                    keycloakWrapper.addRealmRoleToUser(TEACHER_ROLE, user.id.value)
                    logger.info { "updated user ${user.id}" }
                }
            }
    }
}
