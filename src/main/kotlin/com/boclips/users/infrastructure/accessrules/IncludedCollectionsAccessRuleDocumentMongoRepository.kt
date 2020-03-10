package com.boclips.users.infrastructure.accessrules

import org.springframework.data.mongodb.repository.MongoRepository

interface IncludedCollectionsAccessRuleDocumentMongoRepository :
    MongoRepository<AccessRuleDocument.IncludedCollections, String>
