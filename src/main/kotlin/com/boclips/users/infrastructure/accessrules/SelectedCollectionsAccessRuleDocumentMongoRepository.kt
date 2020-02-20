package com.boclips.users.infrastructure.accessrules

import org.springframework.data.mongodb.repository.MongoRepository

interface SelectedCollectionsAccessRuleDocumentMongoRepository :
    MongoRepository<AccessRuleDocument.SelectedCollections, String>
