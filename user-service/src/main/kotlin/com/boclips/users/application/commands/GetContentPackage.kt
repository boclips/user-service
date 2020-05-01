package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.ContentPackageNotFoundException
import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.access.ContentPackageRepository
import org.springframework.stereotype.Service

@Service
class GetContentPackage(private val contentPackageRepository: ContentPackageRepository) {
    operator fun invoke(packageId: String): ContentPackage {
        return contentPackageRepository.findById(ContentPackageId(packageId)) ?: throw ContentPackageNotFoundException(
            packageId
        )
    }
}
