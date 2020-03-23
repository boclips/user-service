package com.boclips.users.presentation.hateoas

import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.presentation.controllers.ContentPackageController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Service

@Service
class ContentPackageLinkBuilder {
    fun self(contentPackageId: ContentPackageId): Link {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(ContentPackageController::class.java).fetchContentPackage(contentPackageId.value)
        ).withRel("self")
    }
}
