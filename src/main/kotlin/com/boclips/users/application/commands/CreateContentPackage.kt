package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccessRuleNotFoundException
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.domain.service.AccessRuleRepository
import com.boclips.users.domain.service.ContentPackageRepository
import com.boclips.users.presentation.requests.CreateContentPackageRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class CreateContentPackage(
    private val contentPackageRepository: ContentPackageRepository,
    private val accessRuleRepository: AccessRuleRepository
) {
    operator fun invoke(createContentPackageRequest: CreateContentPackageRequest): ContentPackage {
        createContentPackageRequest.accessRuleIds.forEach {
            accessRuleRepository.findById(AccessRuleId(it)) ?: throw AccessRuleNotFoundException(it)
        }

        return contentPackageRepository.save(ContentPackage(
            id = ContentPackageId(ObjectId().toHexString()),
            name = createContentPackageRequest.name,
            accessRuleIds = createContentPackageRequest.accessRuleIds.map { AccessRuleId(it) }
        ))
    }
}