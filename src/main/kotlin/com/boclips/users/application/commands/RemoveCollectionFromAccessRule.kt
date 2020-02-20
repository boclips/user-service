package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.AccessRuleNotFoundException
import com.boclips.users.domain.model.accessrules.AccessRule
import com.boclips.users.domain.model.accessrules.AccessRuleId
import com.boclips.users.domain.model.accessrules.CollectionId
import com.boclips.users.domain.service.AccessRuleRepository
import org.springframework.stereotype.Service

@Service
class RemoveCollectionFromAccessRule(private val accessRuleRepository: AccessRuleRepository) {
    operator fun invoke(accessRuleId: AccessRuleId, collectionId: CollectionId) {
        accessRuleRepository.findById(accessRuleId)?.let { accessRule ->
            when (accessRule) {
                is AccessRule.SelectedCollections -> accessRule.collectionIds.filter { it != collectionId }.let {
                    val updatedAccessRule = accessRule.copy(collectionIds = it)
                    accessRuleRepository.save(updatedAccessRule)
                }
            }
        } ?: throw AccessRuleNotFoundException(accessRuleId.value)
    }
}
