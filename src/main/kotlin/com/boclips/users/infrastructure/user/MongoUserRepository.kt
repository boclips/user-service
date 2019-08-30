package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserUpdateCommand
import com.boclips.users.infrastructure.organisation.OrganisationTypeDocument

class MongoUserRepository(
    private val userDocumentMongoRepository: UserDocumentMongoRepository,
    private val userDocumentConverter: UserDocumentConverter
) : UserRepository {

    override fun update(user: User, vararg updateCommands: UserUpdateCommand) {
        val userDocument = UserDocument.from(user)

        updateCommands.map { updateCommand ->
            return@map when (updateCommand) {
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
                // TODO: think about country updates...
                is UserUpdateCommand.ReplaceCountry -> userDocument.apply { country = updateCommand.country }
                is UserUpdateCommand.ReplaceState -> userDocument.apply { state = updateCommand.state }
                is UserUpdateCommand.ReplaceSchool -> userDocument.apply { school = updateCommand.school }
                is UserUpdateCommand.ReplaceOrganisation -> userDocument.apply {
                    organisationType = OrganisationTypeDocument(
                        id = updateCommand.organisationType.getIdentifier()?.value,
                        type = updateCommand.organisationType
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

    private fun saveUserDocument(document: UserDocument) =
        userDocumentConverter.convertToUser(userDocumentMongoRepository.save(document))
}

