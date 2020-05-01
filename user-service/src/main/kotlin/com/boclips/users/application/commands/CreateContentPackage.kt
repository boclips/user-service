package com.boclips.users.application.commands

import com.boclips.users.api.request.CreateContentPackageRequest
import com.boclips.users.application.exceptions.DuplicateContentPackageException
import com.boclips.users.application.exceptions.InvalidCreateContentPackageException
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.access.AccessRuleRepository
import com.boclips.users.domain.model.access.ContentPackageRepository
import com.boclips.users.domain.service.UniqueId
import org.springframework.stereotype.Service

@Service
class CreateContentPackage(
    private val contentPackageRepository: ContentPackageRepository,
    private val accessRuleRepository: AccessRuleRepository
) {
    operator fun invoke(createContentPackageRequest: CreateContentPackageRequest): ContentPackage {
        createContentPackageRequest.accessRuleIds.forEach {
            accessRuleRepository.findById(AccessRuleId(it))
                ?: throw InvalidCreateContentPackageException("Access rule not found for $it")
        }

        contentPackageRepository.findByName(createContentPackageRequest.name)?.let {
            throw DuplicateContentPackageException(it.name)
        }

        return contentPackageRepository.save(ContentPackage(
            id = ContentPackageId(UniqueId()),
            name = createContentPackageRequest.name,
            accessRuleIds = createContentPackageRequest.accessRuleIds.map { AccessRuleId(it) }
        ))
    }
}
