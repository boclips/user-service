package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserUpdateCommand
import com.boclips.users.infrastructure.MongoDatabase
import com.boclips.users.infrastructure.organisation.OrganisationIdResolver
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.and
import mu.KLogging
import org.litote.kmongo.`in`
import org.litote.kmongo.eq
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.ne
import org.litote.kmongo.regex
import org.litote.kmongo.save
import java.util.regex.Pattern

class MongoUserRepository(
    private val mongoClient: MongoClient,
    private val userDocumentConverter: UserDocumentConverter,
    private val organisationIdResolver: OrganisationIdResolver
) : UserRepository {

    companion object : KLogging() {
        const val collectionName = "users"
    }

    private fun getUsersCollection(): MongoCollection<UserDocument> {
        return mongoClient.getDatabase(MongoDatabase.DB_NAME).getCollection<UserDocument>(collectionName)
    }

    override fun update(user: User, vararg updateCommands: UserUpdateCommand): User {
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
                is UserUpdateCommand.ReplaceOrganisationId -> userDocument.apply {
                    organisationId = updateCommand.organisationId.value
                }
                is UserUpdateCommand.ReplaceAccessExpiresOn -> userDocument.apply {
                    accessExpiresOn = updateCommand.accessExpiresOn.toInstant()
                }
                is UserUpdateCommand.ReplaceHasLifetimeAccess -> userDocument.apply {
                    hasLifetimeAccess = updateCommand.hasLifetimeAccess
                }
                is UserUpdateCommand.ReplaceShareCode -> userDocument.apply {
                    shareCode = updateCommand.shareCode
                }
                is UserUpdateCommand.ReplaceRole -> userDocument.apply {
                    role = updateCommand.role
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
                UserDocument::organisationId ne organisationId.value
            )
        )
            .map { document -> userDocumentConverter.convertToUser(document) }
            .toList()
    }

    override fun findAllByOrganisationId(id: OrganisationId): List<User> {
        return getUsersCollection().find(UserDocument::organisationId eq id.value)
            .map { document -> userDocumentConverter.convertToUser(document) }
            .toList()
    }

    override fun findById(id: UserId): User? {
        return getUsersCollection()
            .findOneById(id.value)
            ?.let { userDocumentConverter.convertToUser(it) }
    }

    override fun create(identity: Identity): User {
        val organisationId = organisationIdResolver.resolve(identity.roles)

        val document = UserDocument.from(
            identity = identity,
            organisationId = organisationId
        )

        return saveUserDocument(document)
    }

    override fun create(user: User) = saveUserDocument(UserDocument.from(user))

    private fun saveUserDocument(document: UserDocument): User {
        getUsersCollection().save(document)
        return findById(UserId(document._id)) ?: throw IllegalStateException("this should never happen")
    }
}

