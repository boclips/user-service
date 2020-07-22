package com.boclips.users.presentation.controllers

import com.boclips.users.api.request.UpdateContentPackageRequest
import com.boclips.users.api.response.accessrule.ContentPackageResource
import com.boclips.users.api.response.accessrule.ContentPackagesResource
import com.boclips.users.application.commands.GetContentPackage
import com.boclips.users.application.commands.GetContentPackages
import com.boclips.users.application.commands.UpdateContentPackage
import com.boclips.users.presentation.converters.ContentPackageConverter
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1")
class ContentPackageController(
    private val contentPackageConverter: ContentPackageConverter,
    private val getContentPackage: GetContentPackage,
    private val getContentPackages: GetContentPackages,
    private val updateContentPackage: UpdateContentPackage
) {
    @GetMapping("/content-packages")
    fun fetchContentPackages(): ContentPackagesResource {
        return contentPackageConverter.toContentPackagesResource(getContentPackages())
    }

    @GetMapping("/content-packages/{id}")
    fun fetchContentPackage(@PathVariable("id") id: String?): ContentPackageResource {
        return contentPackageConverter.toContentPackageResource(
            getContentPackage(id!!)
        )
    }

    @PutMapping("/content-packages/{id}")
    fun updatePackage(@PathVariable("id") id: String, @RequestBody contentPackageUpdateRequest: UpdateContentPackageRequest): ContentPackageResource {
        return contentPackageConverter.toContentPackageResource(
            updateContentPackage(id, contentPackageUpdateRequest)
        )
    }
}
