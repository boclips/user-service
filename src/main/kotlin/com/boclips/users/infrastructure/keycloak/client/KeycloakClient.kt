package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.infrastructure.keycloak.KeycloakUser
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import com.boclips.users.infrastructure.keycloak.UserNotCreatedException
import com.boclips.users.infrastructure.keycloak.client.exceptions.InvalidUserRepresentation
import mu.KLogging
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.retry.annotation.Retryable

open class KeycloakClient(
    private val keycloak: KeycloakWrapper,
    private val userConverter: KeycloakUserToUserIdentityConverter
) : IdentityProvider {
    companion object : KLogging()

    @Retryable(value = [UserNotCreatedException::class], maxAttempts = 2)
    override fun createUser(firstName: String, lastName: String, email: String, password: String): Identity {
        val createdUser = keycloak.createUser(
            KeycloakUser(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            )
        )
        logger.info { "Created user ${createdUser.id} in Keycloak" }

        return Identity(
            id = UserId(value = createdUser.id),
            firstName = createdUser.firstName,
            lastName = createdUser.lastName,
            email = createdUser.email,
            isVerified = createdUser.isEmailVerified
        )
    }

    override fun getUserById(id: UserId): Identity? {
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

    override fun count(): Int {
        return keycloak.countUsers()
    }
}
