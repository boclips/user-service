package com.boclips.users.infrastructure.contract

import org.springframework.data.mongodb.repository.MongoRepository

interface SelectedCollectionsContractDocumentMongoRepository :
    MongoRepository<ContractDocument.SelectedCollections, String>