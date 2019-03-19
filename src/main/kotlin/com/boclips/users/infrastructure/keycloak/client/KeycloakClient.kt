package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.infrastructure.keycloak.KeycloakUser
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import com.boclips.users.infrastructure.keycloak.client.exceptions.InvalidUserRepresentation
import mu.KLogging
import org.keycloak.representations.idm.UserRepresentation
import java.time.LocalDate

class KeycloakClient(
    private val keycloak: KeycloakWrapper,
    private val userConverter: KeycloakUserToUserIdentityConverter
) : IdentityProvider {
    companion object : KLogging() {
        const val REALM = "boclips"
        const val TEACHERS_GROUP_NAME: String = "teachers"
    }

    override fun createNewUser(firstName: String, lastName: String, email: String, password: String): Identity {
        val createdUser = keycloak.createUser(
            KeycloakUser(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            )
        )
        logger.info { "Created user ${createdUser.id}" }

        keycloak.sendVerificationEmail(createdUser.id)
        logger.info { "Sent verification email to user ${createdUser.id}" }

        return Identity(
            id = IdentityId(value = createdUser.id),
            firstName = createdUser.firstName,
            lastName = createdUser.lastName,
            email = createdUser.email,
            isEmailVerified = createdUser.isEmailVerified
        )
    }

    override fun getUserById(id: IdentityId): Identity? {
        val user: UserRepresentation?
        return try {
            user = keycloak.getUser(id.value)!!
            userConverter.convert(user)
        } catch (e: javax.ws.rs.NotFoundException) {
            logger.warn { "Could not find user: ${id.value}, omitting user" }
            null
        } catch (e: InvalidUserRepresentation) {
            logger.warn { "Could not convert external keycloak user: ${id.value}, omitting user because: ${e.message}" }
            null
        } catch (e: Exception) {
            logger.warn(e) { "Unexpected exception happened when looking up user: ${id.value}, omitting user" }
            null
        }
    }

    override fun getNewTeachers(since: LocalDate): List<Identity> {
        return keycloak.getRegisterEvents(since)
            .mapNotNull { it.userId }
            .filter { userId ->
                try {
                    val groupNames = keycloak.getGroupsOfUser(userId).map { group -> group.name }
                    if (groupNames.contains(TEACHERS_GROUP_NAME)) {
                        true
                    } else {
                        logger.error { "User $userId not in teachers group" }
                        false
                    }
                } catch (e: Exception) {
                    logger.error(e) { "Could not find newly created user $userId" }
                    false
                }
            }
            .mapNotNull { getUserById(IdentityId(value = it)) }
    }

    override fun getUsers(): List<Identity> {
        return keycloak.users().mapNotNull {
            try {
                userConverter.convert(it)
            } catch (e: InvalidUserRepresentation) {
                logger.warn { "Could not convert external keycloak user as email address is invalid" }
                null
            }
        }
    }
}
