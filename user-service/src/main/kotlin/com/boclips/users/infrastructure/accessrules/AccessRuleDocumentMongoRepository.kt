package com.boclips.users.infrastructure.accessrules

import org.springframework.data.mongodb.repository.MongoRepository

interface AccessRuleDocumentMongoRepository : MongoRepository<AccessRuleDocument, String> {
    fun findByName(name: String): List<AccessRuleDocument>
}
