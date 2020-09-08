package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.model.user.UserUpdate
import com.boclips.users.infrastructure.MongoDatabase
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import com.boclips.users.infrastructure.organisation.OrganisationDocument
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter
import com.mongodb.MongoClient
import com.mongodb.MongoWriteException
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.and
import mu.KLogging
import org.bson.types.ObjectId
import org.litote.kmongo.`in`
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.ne
import org.litote.kmongo.or
import org.litote.kmongo.regex
import org.litote.kmongo.save
import java.util.regex.Pattern

class MongoUserRepository(
    private val mongoClient: MongoClient,
    private val userDocumentConverter: UserDocumentConverter
) : UserRepository {
    companion object : KLogging()

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

        getUsersCollection().save(userDocument)
        return findById(UserId(userDocument._id)) ?: throw IllegalStateException("this should never happen")
    }

    override fun findAll(ids: List<UserId>) = getUsersCollection()
        .find(UserDocument::_id `in` ids.map { it.value })
        .mapNotNull { userDocumentConverter.convertToUser(it) }

    override fun findAll(): List<User> {
        return getUsersCollection().find().convert()
    }

    override fun findAllTeachers(): List<User> {
        return getUsersCollection()
            .find(
                or(
                    UserDocument::organisation / OrganisationDocument::type eq OrganisationType.SCHOOL,
                    UserDocument::organisation / OrganisationDocument::type eq OrganisationType.DISTRICT,
                    UserDocument::organisation eq null
                )
            )
            .convert()
    }

    override fun findOrphans(domain: String, organisationId: OrganisationId): List<User> {
        logger.info { ">> findOrphans" }

        val query = and(
            UserDocument::email regex ".+@${Pattern.quote(domain)}$",
            UserDocument::organisation / OrganisationDocument::_id ne ObjectId(organisationId.value)
        )

        logger.info { "query: $query"  }
        return getUsersCollection().find(query)
            .convert()
    }

    override fun findAllByOrganisationId(id: OrganisationId): List<User> {
        return getUsersCollection().find(
            UserDocument::organisation / OrganisationDocument::_id eq ObjectId(id.value)
        )
            .convert()
    }

    override fun findById(id: UserId): User? {
        return getUsersCollection()
            .findOneById(id.value)
            ?.let { userDocumentConverter.convertToUser(it) }
    }

    override fun create(user: User): User {
        try {
            getUsersCollection().insertOne(UserDocument.from(user))
        } catch (e: MongoWriteException) {
            // Creation might fail if an other process already inserted the user around the same time
            // See: https://jira.mongodb.org/browse/SERVER-22607
            findById(user.id) ?: throw RuntimeException("Unable to save user: ${user.id}", e)
            throw UserAlreadyExistsException()
        }
        return findById(user.id) ?: throw IllegalStateException("this should never happen")
    }

    private fun FindIterable<UserDocument>.convert(): List<User> {
        return this
            .map { document -> userDocumentConverter.convertToUser(document) }
            .toList()
    }
}
