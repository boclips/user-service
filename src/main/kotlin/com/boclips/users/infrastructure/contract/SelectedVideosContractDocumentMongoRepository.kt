package com.boclips.users.infrastructure.contract

import org.springframework.data.mongodb.repository.MongoRepository

interface SelectedVideosContractDocumentMongoRepository :
    MongoRepository<ContractDocument.SelectedVideos, String>