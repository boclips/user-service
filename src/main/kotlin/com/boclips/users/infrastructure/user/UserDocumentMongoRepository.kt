package com.boclips.users.infrastructure.user

import org.springframework.data.mongodb.repository.MongoRepository

interface UserDocumentMongoRepository : MongoRepository<UserDocument, String>