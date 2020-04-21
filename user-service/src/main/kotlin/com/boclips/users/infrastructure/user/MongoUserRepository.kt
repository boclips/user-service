package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserUpdate
import com.boclips.users.infrastructure.MongoDatabase
import com.boclips.users.infrastructure.organisation.OrganisationDocument
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.and
import org.bson.types.ObjectId
import org.litote.kmongo.`in`
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.ne
import org.litote.kmongo.regex
import org.litote.kmongo.save
import java.util.regex.Pattern

class MongoUserRepository(
    private val mongoClient: MongoClient,
    private val userDocumentConverter: UserDocumentConverter
) : UserRepository {

    private fun getUsersCollection(): MongoCollection<UserDocument> {
        return mongoClient.getDatabase(MongoDatabase.DB_NAME).getCollection<UserDocument>("users")
    }

    override fun update(user: User, vararg updates: UserUpdate): User {
        val userDocument = UserDocument.from(user)

        updates.map { updateCommand ->
            return@map when (updateCommand) {
                is UserUpdate.ReplaceFirstName -> userDocument.apply { firstName = updateCommand.firstName }
                is UserUpdate.ReplaceLastName -> userDocument.apply { lastName = updateCommand.lastName }
                is UserUpdate.ReplaceSubjects -> userDocument.apply {
                    subjectIds = updateCommand.subjects.map { it.id.value }
                }
                is UserUpdate.ReplaceAges -> userDocument.apply { ageRange = updateCommand.ages }
                is UserUpdate.ReplaceHasOptedIntoMarketing -> userDocument.apply {
                    hasOptedIntoMarketing = updateCommand.hasOptedIntoMarketing
                }
                is UserUpdate.ReplaceReferralCode -> userDocument.apply {
                    referralCode = updateCommand.referralCode
                }
                is UserUpdate.ReplaceMarketingTracking -> userDocument.apply {
                    marketing = MarketingTrackingDocument(
                        utmCampaign = updateCommand.utmCampaign,
                        utmSource = updateCommand.utmSource,
                        utmContent = updateCommand.utmContent,
                        utmMedium = updateCommand.utmMedium,
                        utmTerm = updateCommand.utmTerm
                    )
                }
                is UserUpdate.ReplaceOrganisation -> userDocument.apply {
                    organisation = OrganisationDocumentConverter.toDocument(updateCommand.organisation)
                }
                is UserUpdate.ReplaceAccessExpiresOn -> userDocument.apply {
                    accessExpiresOn = updateCommand.accessExpiresOn.toInstant()
                }
                is UserUpdate.ReplaceHasLifetimeAccess -> userDocument.apply {
                    hasLifetimeAccess = updateCommand.hasLifetimeAccess
                }
                is UserUpdate.ReplaceShareCode -> userDocument.apply {
                    shareCode = updateCommand.shareCode
                }
                is UserUpdate.ReplaceRole -> userDocument.apply {
                    role = updateCommand.role
                }
                is UserUpdate.ReplaceProfileSchool -> userDocument.apply {
                    profileSchool = OrganisationDocumentConverter.toDocument(updateCommand.school)
                }
            }
        }

        return saveUserDocument(userDocument)
    }

    override fun findAll(ids: List<UserId>) = getUsersCollection()
        .find(UserDocument::_id `in` ids.map { it.value })
        .mapNotNull { userDocumentConverter.convertToUser(it) }

    override fun findAll(): List<User> {
        return getUsersCollection().find().map { document -> userDocumentConverter.convertToUser(document) }.toList()
    }

    override fun findOrphans(domain: String, organisationId: OrganisationId): List<User> {
        return getUsersCollection().find(
            and(
                UserDocument::email regex ".+@${Pattern.quote(domain)}$",
                UserDocument::organisation / OrganisationDocument::_id ne ObjectId(organisationId.value)
            )
        )
            .map { document -> userDocumentConverter.convertToUser(document) }
            .toList()
    }

    override fun findAllByOrganisationId(id: OrganisationId): List<User> {
        return getUsersCollection().find(
            UserDocument::organisation / OrganisationDocument::_id eq ObjectId(id.value)
        )
            .map { document -> userDocumentConverter.convertToUser(document) }
            .toList()
    }

    override fun findById(id: UserId): User? {
        return getUsersCollection()
            .findOneById(id.value)
            ?.let { userDocumentConverter.convertToUser(it) }
    }

    override fun create(user: User) = saveUserDocument(UserDocument.from(user))

    private fun saveUserDocument(document: UserDocument): User {
        getUsersCollection().save(document)
        return findById(UserId(document._id)) ?: throw IllegalStateException("this should never happen")
    }
}

