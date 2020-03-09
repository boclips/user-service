package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccessRuleNotFoundException
import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.service.AccessRuleRepository
import org.springframework.stereotype.Service

@Service
class RemoveCollectionFromAccessRule(private val accessRuleRepository: AccessRuleRepository) {
    operator fun invoke(accessRuleId: AccessRuleId, collectionId: CollectionId) {
        accessRuleRepository.findById(accessRuleId)?.let { accessRule ->
            if (accessRule is AccessRule.IncludedCollections) {
                accessRule.collectionIds.filter { it != collectionId }.let {
                    val updatedAccessRule = accessRule.copy(collectionIds = it)
                    accessRuleRepository.save(updatedAccessRule)
                }
            }
        } ?: throw AccessRuleNotFoundException(accessRuleId.value)
    }
}
