package com.boclips.users.presentation.controllers

import com.boclips.users.api.request.SynchroniseIntegrationUserRequest
import com.boclips.users.api.response.integration.SynchUserResource
import com.boclips.users.application.commands.SynchroniseIntegrationUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/integrations")
class IntegrationController(
    val synchroniseIntegrationUser: SynchroniseIntegrationUser
) {

    @PutMapping("/deployments")
    fun synchroniseUser(
        @RequestBody request: SynchroniseIntegrationUserRequest
    ): ResponseEntity<SynchUserResource> {
        val user = try {
            synchroniseIntegrationUser(
                deploymentId = request.deploymentId,
                externalUserId = request.externalUserId
            )
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(SynchUserResource(userId = user.id.value), HttpStatus.OK)
    }

    @PutMapping("/deployments/{deploymentId}/users/{externalUserId}")
    fun synchroniseUser(
        @PathVariable deploymentId: String,
        @PathVariable externalUserId: String,
    ): ResponseEntity<SynchUserResource> {
        val user = try {
            synchroniseIntegrationUser(
                deploymentId = deploymentId,
                externalUserId = externalUserId
            )
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(SynchUserResource(userId = user.id.value), HttpStatus.OK)
    }
}
