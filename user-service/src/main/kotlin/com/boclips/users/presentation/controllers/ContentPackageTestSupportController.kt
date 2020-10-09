package com.boclips.users.presentation.controllers

import com.boclips.users.api.request.CreateContentPackageRequest
import com.boclips.users.application.commands.CreateContentPackage
import com.boclips.users.presentation.annotations.BoclipsE2ETestSupport
import com.boclips.users.presentation.hateoas.ContentPackageLinkBuilder
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@BoclipsE2ETestSupport
@RestController
@RequestMapping("/v1/content-packages")
class ContentPackageTestSupportController(
    private val contentPackageLinkBuilder: ContentPackageLinkBuilder,
    private val createContentPackage: CreateContentPackage
) {
    @PostMapping
    fun insertContentPackage(@RequestBody createContentPackageRequest: CreateContentPackageRequest): ResponseEntity<EntityModel<*>> {
        val contentPackage = createContentPackage(createContentPackageRequest)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, contentPackageLinkBuilder.self(contentPackage.id).href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }
}
