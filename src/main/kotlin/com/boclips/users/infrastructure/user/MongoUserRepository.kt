package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserCounts
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserUpdateCommand
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

class MongoUserRepository(
    private val userDocumentMongoRepository: UserDocumentMongoRepository,
    private val userDocumentConverter: UserDocumentConverter
) : UserRepository {

    override fun update(user: User, vararg updateCommands: UserUpdateCommand) {
        val userDocument = UserDocument.from(user)

        updateCommands.forEach { updateCommand ->
            when (updateCommand) {
                is UserUpdateCommand.ReplaceFirstName -> userDocument.apply { firstName = updateCommand.firstName }
                is UserUpdateCommand.ReplaceLastName -> userDocument.apply { lastName = updateCommand.lastName }
                is UserUpdateCommand.ReplaceSubjects -> userDocument.apply {
                    subjectIds = updateCommand.subjects.map { it.id.value }
                }
                is UserUpdateCommand.ReplaceAges -> userDocument.apply { ageRange = updateCommand.ages }
                is UserUpdateCommand.ReplaceHasOptedIntoMarketing -> userDocument.apply {
                    hasOptedIntoMarketing = updateCommand.hasOptedIntoMarketing
                }
                is UserUpdateCommand.ReplaceReferralCode -> userDocument.apply {
                    referralCode = updateCommand.referralCode
                }
                is UserUpdateCommand.ReplaceMarketingTracking -> userDocument.apply {
                    marketing = MarketingTrackingDocument(
                        utmCampaign = updateCommand.utmCampaign,
                        utmSource = updateCommand.utmSource,
                        utmContent = updateCommand.utmContent,
                        utmMedium = updateCommand.utmMedium,
                        utmTerm = updateCommand.utmTerm
                    )
                }
            }
        }

        userDocumentMongoRepository.save(userDocument)
    }

    override fun findAll(ids: List<UserId>) = userDocumentMongoRepository
        .findAllById(ids.map { it.value })
        .mapNotNull { userDocumentConverter.convertToUser(it) }

    override fun findAll(): List<User> {
        return userDocumentMongoRepository.findAll().map { document -> userDocumentConverter.convertToUser(document) }
    }

    override fun findById(id: UserId): User? {
        return userDocumentMongoRepository
            .findById(id.value)
            .orElse(null)
            ?.let { userDocumentConverter.convertToUser(it) }
    }

    override fun save(account: Account) = saveUserDocument(UserDocument.from(account))

    override fun save(user: User) = saveUserDocument(UserDocument.from(user))

    // TODO: ideally ditch count and handle aggregates in data-land / stepping stone: ditch activated
    override fun count(): UserCounts {
        val total = userDocumentMongoRepository.count()
        val activated = userDocumentMongoRepository.countByFirstNameIsNotNull()
        return UserCounts(total = total, activated = activated)
    }

    private fun saveUserDocument(document: UserDocument) =
        userDocumentConverter.convertToUser(userDocumentMongoRepository.save(document))
}

