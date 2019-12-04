package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserUpdateCommand
import com.boclips.users.infrastructure.organisation.OrganisationIdResolver
import java.time.Instant

class MongoUserRepository(
    private val userDocumentMongoRepository: UserDocumentMongoRepository,
    private val userDocumentConverter: UserDocumentConverter,
    private val organisationIdResolver: OrganisationIdResolver
) : UserRepository {

    override fun update(user: User, vararg updateCommands: UserUpdateCommand): User {
        val userDocument = userDocumentConverter.convertToDocument(user)

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
                is UserUpdateCommand.ReplaceOrganisationId -> userDocument.apply {
                    organisationId = updateCommand.organisationAccountId.value
                }
                is UserUpdateCommand.ReplaceAccessExpiresOn -> userDocument.apply {
                    accessExpiresOn = updateCommand.accessExpiresOn.toInstant()
                }
            }
        }

        return saveUserDocument(userDocument)
    }

    override fun findAll(ids: List<UserId>) = userDocumentMongoRepository
        .findAllById(ids.map { it.value })
        .mapNotNull { userDocumentConverter.convertToUser(it) }

    override fun findAll(): List<User> {
        return userDocumentMongoRepository.findAll().map { document -> userDocumentConverter.convertToUser(document) }
    }

    override fun findAllByOrganisationId(organisationId: OrganisationAccountId): List<User> {
        return userDocumentMongoRepository.findByOrganisationId(organisationId.value)
            .map { document -> userDocumentConverter.convertToUser(document) }
    }

    override fun findById(id: UserId): User? {
        return userDocumentMongoRepository
            .findById(id.value)
            .orElse(null)
            ?.let { userDocumentConverter.convertToUser(it) }
    }

    override fun create(account: Account): User {
        val organisationAccountId = organisationIdResolver.resolve(account.roles)

        val document = UserDocument.create(
            account = account,
            organisationAccountId = organisationAccountId,
            createdAt = Instant.now()
        )

        return saveUserDocument(document)
    }

    override fun create(user: User) = saveUserDocument(UserDocument.create(user, createdAt = Instant.now()))

    private fun saveUserDocument(document: UserDocument) =
        userDocumentConverter.convertToUser(userDocumentMongoRepository.save(document))
}

