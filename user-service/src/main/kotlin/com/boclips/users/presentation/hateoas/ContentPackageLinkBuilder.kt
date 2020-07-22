package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.access.ContentPackageId
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

    fun getContentPackagesLink(): Link? {
        return if (UserExtractor.currentUserHasAnyRole(UserRoles.VIEW_CONTENT_PACKAGES)) {
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ContentPackageController::class.java).fetchContentPackages()
            ).withRel("contentPackages")
        } else {
            null
        }

    }
    fun getContentPackageLink(): Link? {
        return if (UserExtractor.currentUserHasAnyRole(UserRoles.VIEW_CONTENT_PACKAGES)) {
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ContentPackageController::class.java).fetchContentPackage(null)
            ).withRel("contentPackage")
        } else {
            null
        }

    }
}
