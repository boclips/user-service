package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.service.AccessRuleRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class MongoAccessRuleRepository(
    private val accessRuleDocumentMongoRepository: AccessRuleDocumentMongoRepository,
    private val accessRuleDocumentConverter: AccessRuleDocumentConverter
) : AccessRuleRepository {
    override fun findById(id: AccessRuleId): AccessRule? {
        return asNullable(accessRuleDocumentMongoRepository.findById(id.value))?.let {
            return accessRuleDocumentConverter.fromDocument(it)
        }
    }

    override fun findAll(): List<AccessRule> {
        return accessRuleDocumentMongoRepository.findAll().map(accessRuleDocumentConverter::fromDocument)
    }

    override fun findAllByName(name: String): List<AccessRule> {
        return accessRuleDocumentMongoRepository.findByName(name).map(accessRuleDocumentConverter::fromDocument)
    }

    override fun save(accessRule: AccessRule): AccessRuleDocument {
        return accessRuleDocumentMongoRepository.save(accessRuleDocumentConverter.toDocument(accessRule))
    }

    private fun asNullable(potentialDocument: Optional<AccessRuleDocument>): AccessRuleDocument? {
        return if (potentialDocument.isPresent) {
            potentialDocument.get()
        } else {
            null
        }
    }
}
