package com.boclips.users.application.commands

import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.ContentPackageRepository
import org.springframework.stereotype.Service

@Service
class GetAccessRules(
    private val contentPackageRepository: ContentPackageRepository
) {
    operator fun invoke(): List<AccessRule> {
        return contentPackageRepository.findAll().map { it.accessRules }.flatten()
    }
}
