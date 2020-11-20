package com.boclips.users.presentation.controllers

import com.boclips.users.api.request.CreateContentPackageRequest
import com.boclips.users.api.request.UpdateContentPackageRequest
import com.boclips.users.api.response.accessrule.ContentPackageResource
import com.boclips.users.api.response.accessrule.ContentPackagesResource
import com.boclips.users.application.commands.CreateContentPackage
import com.boclips.users.application.commands.GetContentPackage
import com.boclips.users.application.commands.GetContentPackages
import com.boclips.users.application.commands.UpdateContentPackage
import com.boclips.users.presentation.converters.ContentPackageConverter
import com.boclips.users.presentation.hateoas.ContentPackageLinkBuilder
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/content-packages")
class ContentPackageController(
    private val contentPackageConverter: ContentPackageConverter,
    private val getContentPackage: GetContentPackage,
    private val getContentPackages: GetContentPackages,
    private val updateContentPackage: UpdateContentPackage,
    private val createContentPackage: CreateContentPackage,
    private val contentPackageLinkBuilder: ContentPackageLinkBuilder
) {
    @GetMapping
    fun fetchContentPackages(): ContentPackagesResource {
        return contentPackageConverter.toContentPackagesResource(getContentPackages())
    }

    @GetMapping("{id}")
    fun fetchContentPackage(@PathVariable id: String?): ContentPackageResource {
        return contentPackageConverter.toContentPackageResource(
            getContentPackage(id!!)
        )
    }

    @PostMapping
    fun insertContentPackage(@RequestBody createContentPackageRequest: CreateContentPackageRequest): ResponseEntity<EntityModel<*>> {
        val contentPackage = createContentPackage(createContentPackageRequest)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, contentPackageLinkBuilder.self(contentPackage.id).href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @PutMapping("{id}")
    fun updatePackage(
        @PathVariable id: String?,
        @RequestBody contentPackageUpdateRequest: UpdateContentPackageRequest?
    ): ContentPackageResource {
        return contentPackageConverter.toContentPackageResource(
            updateContentPackage(id!!, contentPackageUpdateRequest!!)
        )
    }
}
