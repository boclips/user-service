package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.GetContentPackage
import com.boclips.users.presentation.resources.ContentPackageResource
import com.boclips.users.presentation.resources.converters.ContentPackageConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/content-packages")
class ContentPackageController(
    private val contentPackageConverter: ContentPackageConverter,
    private val getContentPackage: GetContentPackage
) {
    @GetMapping("/{id}")
    fun fetchContentPackage(@PathVariable("id") id: String): ContentPackageResource {
        return contentPackageConverter.toResource(
            getContentPackage(id)
        )
    }
}
