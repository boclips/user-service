package com.boclips.users.infrastructure.account

import org.springframework.data.mongodb.repository.MongoRepository

interface UserDocumentMongoRepository : MongoRepository<UserDocument, String>