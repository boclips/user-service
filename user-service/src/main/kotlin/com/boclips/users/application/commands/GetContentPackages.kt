package com.boclips.users.application.commands

import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.domain.model.access.ContentPackageRepository
import org.springframework.stereotype.Service

@Service
class GetContentPackages(private val contentPackageRepository: ContentPackageRepository) {
    operator fun invoke(): List<ContentPackage> {
        return contentPackageRepository.findAll()
    }
}
