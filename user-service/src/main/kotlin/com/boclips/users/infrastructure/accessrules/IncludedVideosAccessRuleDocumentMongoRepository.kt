package com.boclips.users.infrastructure.accessrules

import org.springframework.data.mongodb.repository.MongoRepository

interface IncludedVideosAccessRuleDocumentMongoRepository :
    MongoRepository<AccessRuleDocument.IncludedVideos, String>
