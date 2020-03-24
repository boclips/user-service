package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.infrastructure.keycloak.KeycloakUser
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import com.boclips.users.infrastructure.keycloak.UnknownUserSourceException
import com.boclips.users.infrastructure.keycloak.UserNotCreatedException
import com.boclips.users.infrastructure.keycloak.client.exceptions.InvalidUserRepresentation
import mu.KLogging
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.retry.annotation.Retryable
import java.time.Instant

open class KeycloakClient(
    private val keycloak: KeycloakWrapper,
    private val userConverter: KeycloakUserToAccountConverter
) : IdentityProvider, SessionProvider {
    companion object : KLogging()

    @Retryable(value = [UserNotCreatedException::class], maxAttempts = 2)
    override fun createIdentity(email: String, password: String): Identity {
        val createdUser = keycloak.createUser(
            KeycloakUser(
                email = email,
                password = password
            )
        )
        logger.info { "Created user ${createdUser.id} in Keycloak" }

        return userConverter.convert(createdUser)
    }

    override fun getIdentitiesById(id: UserId): Identity? {
        val user: UserRepresentation?
        return try {
            user = keycloak.getUserById(id.value)!!
            userConverter.convert(user)
        } catch (e: javax.ws.rs.NotFoundException) {
            logger.warn { "Could not find user: ${id.value}, omitting user" }
            null
        } catch (e: InvalidUserRepresentation) {
            logger.info { "Could not convert external keycloak user: ${id.value}, omitting user because: ${e.message}" }
            null
        } catch (e: Exception) {
            logger.info(e) { "Unexpected exception happened when looking up user: ${id.value}, omitting user" }
            null
        }
    }

    override fun getIdentity(): Sequence<Identity> {
        return keycloak.users().mapNotNull { userRepresentation ->
            try {
                userConverter.convert(userRepresentation)
            } catch (e: InvalidUserRepresentation) {
                logger.info { "Could not convert external keycloak user ${userRepresentation.id} as email address is invalid" }
                null
            } catch (e: UnknownUserSourceException) {
                logger.info { "Could not convert keycloak user ${userRepresentation.id} as source is unknown" }
                null
            }
        }
    }

    override fun count(): Int {
        return keycloak.countUsers()
    }

    override fun getUserSessions(id: UserId): UserSessions {
        val loginEventRepresentations = keycloak.getLastUserSession(id.value)

        if (loginEventRepresentations.isEmpty()) {
            return UserSessions(lastAccess = null)
        }

        return UserSessions(lastAccess = Instant.ofEpochMilli(loginEventRepresentations.first().time))
    }
}