package com.boclips.users.application.commands

import com.boclips.users.api.request.CreateContentPackageRequest
import com.boclips.users.application.exceptions.DuplicateContentPackageException
import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.access.ContentPackageRepository
import com.boclips.users.domain.service.UniqueId
import com.boclips.users.presentation.converters.AccessRuleConverter
import org.springframework.stereotype.Service

@Service
class CreateContentPackage(
    private val contentPackageRepository: ContentPackageRepository,
    private val accessRuleConverter: AccessRuleConverter
) {
    operator fun invoke(createContentPackageRequest: CreateContentPackageRequest): ContentPackage {
        contentPackageRepository.findByName(createContentPackageRequest.name)?.let {
            throw DuplicateContentPackageException(it.name)
        }

        return contentPackageRepository.save(ContentPackage(
            id = ContentPackageId(UniqueId()),
            name = createContentPackageRequest.name,
            accessRules = createContentPackageRequest.accessRules.map { accessRuleConverter.fromRequest(it) }
        ))
    }
}
