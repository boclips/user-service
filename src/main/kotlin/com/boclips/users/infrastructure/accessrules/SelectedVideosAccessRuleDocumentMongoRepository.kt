package com.boclips.users.infrastructure.accessrules

import org.springframework.data.mongodb.repository.MongoRepository

interface SelectedVideosAccessRuleDocumentMongoRepository :
    MongoRepository<AccessRuleDocument.SelectedVideos, String>
