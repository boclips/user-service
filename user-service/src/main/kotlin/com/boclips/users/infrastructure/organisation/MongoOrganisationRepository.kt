package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.Page
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.organisation.OrganisationTag
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.OrganisationUpdate
import com.boclips.users.domain.model.organisation.OrganisationUpdate.AddTag
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceBilling
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceContentPackageId
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceDomain
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceExpiryDate
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceFeatures
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.infrastructure.MongoDatabase
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter.fromDocument
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.litote.kmongo.`in`
import org.litote.kmongo.and
import org.litote.kmongo.contains
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.ne
import org.litote.kmongo.orderBy
import org.litote.kmongo.regex
import org.litote.kmongo.save
import org.litote.kmongo.setValue
import java.util.regex.Pattern

class MongoOrganisationRepository(
    private val mongoClient: MongoClient
) : OrganisationRepository {

    private fun collection(): MongoCollection<OrganisationDocument> {
        return mongoClient.getDatabase(MongoDatabase.DB_NAME).getCollection<OrganisationDocument>(
            "organisations"
        )
    }

    override fun lookupSchools(
        schoolName: String,
        countryCode: String
    ): List<School> {
        return collection()
            .find(
                and(
                    OrganisationDocument::country / LocationDocument::code eq countryCode,
                    OrganisationDocument::name.regex(Pattern.quote(schoolName), "i"),
                    OrganisationDocument::type eq OrganisationType.SCHOOL
                )
            )
            .toList().convert()
    }

    override fun findByRoleIn(roles: List<String>): List<Organisation> {
        return collection()
            .find(OrganisationDocument::role `in` roles)
            .convert()
    }

    override fun findByTag(tag: OrganisationTag): List<Organisation> {
        return collection()
            .find(OrganisationDocument::tags contains tag.name)
            .convert()
    }

    override fun findByEmailDomain(domain: String): List<Organisation> {
        return collection()
            .find(OrganisationDocument::domain eq domain)
            .convert()
    }

    override fun <T : Organisation> save(organisation: T): T {
        return save(OrganisationDocumentConverter.toDocument(organisation))
            .cast()
    }

    private fun save(organisationDocument: OrganisationDocument): Organisation {
        collection().save(organisationDocument)
        collection().updateMany(
            OrganisationDocument::parent / OrganisationDocument::_id eq organisationDocument._id!!,
            setValue(OrganisationDocument::parent, organisationDocument)
        )
        return findOrganisationById(OrganisationId(organisationDocument._id.toHexString()))!!
    }

    override fun update(id: OrganisationId, vararg updates: OrganisationUpdate): Organisation? {
        val document = collection().findOneById(ObjectId(id.value)) ?: return null

        val updatedDocument = updates.fold(document) { accumulator: OrganisationDocument, update: OrganisationUpdate ->
            return@fold when (update) {
                is ReplaceExpiryDate -> accumulator.copy(accessExpiresOn = update.accessExpiresOn.toInstant())
                is ReplaceDomain -> accumulator.copy(domain = update.domain)
                is AddTag -> accumulator.copy(tags = accumulator.tags.orEmpty() + update.tag.name)
                is ReplaceBilling -> accumulator.copy(billing = update.billing)
                is ReplaceFeatures ->
                    accumulator.copy(
                        features = update.features.mapKeys {
                            FeatureDocumentConverter.toDocument(it.key)
                        }
                    )
                is ReplaceContentPackageId -> accumulator.copy(contentPackageId = update.contentPackageId.value)
            }
        }

        return save(updatedDocument)
    }

    override fun findOrganisationsByParentId(parentId: OrganisationId): List<Organisation> {
        return collection().find(OrganisationDocument::parent / OrganisationDocument::_id eq ObjectId(parentId.value))
            .convert()
    }

    override fun findOrganisationById(id: OrganisationId): Organisation? {
        return collection().findOneById(ObjectId(id.value))?.convert()
    }

    override fun findSchoolById(id: OrganisationId): School? {
        return findOrganisationById(id)
            ?.let { it as? School? }
    }

    override fun findByLegacyId(legacyId: String): Organisation? {
        return collection().findOne(OrganisationDocument::legacyId eq legacyId)?.convert()
    }

    override fun findOrganisations(
        name: String?,
        countryCode: String?,
        types: List<OrganisationType>?,
        page: Int,
        size: Int,
        hasCustomPrices: Boolean?
    ): Page<Organisation> {

        val filter = query(
            name = name,
            countryCode = countryCode,
            types = types,
            hasCustomPrices = hasCustomPrices
        )

        val totalElements = collection().countDocuments(filter)

        val results = collection()
            .find(filter)
            .sort(sort())
            .skip(page * size)
            .limit(size)
            .convert<Organisation>()

        return Page(
            items = results,
            pageSize = size,
            pageNumber = page,
            totalElements = totalElements
        )
    }

    override fun findApiIntegrationByName(name: String): ApiIntegration? {
        return collection().findOne(
            and(
                OrganisationDocument::name eq name,
                OrganisationDocument::type eq OrganisationType.API
            )
        )
            ?.convert()
    }

    override fun findDistrictByName(name: String): District? {
        return collection().findOne(
            and(
                OrganisationDocument::name eq name,
                OrganisationDocument::type eq OrganisationType.DISTRICT
            )
        )
            ?.convert()
    }

    override fun findOrganisationByExternalId(id: ExternalOrganisationId): Organisation? {
        return collection().findOne(OrganisationDocument::externalId eq id.value)?.convert()
    }

    private fun query(
        name: String?,
        countryCode: String?,
        types: List<OrganisationType>?,
        hasCustomPrices: Boolean?
    ): Bson {

        return and(
            listOfNotNull(
                name?.let {
                    OrganisationDocument::name.regex(".*$it.*", "i")
                },
                countryCode?.let {
                    OrganisationDocument::country / LocationDocument::code eq it
                },
                types?.let {
                    OrganisationDocument::type `in` it
                },
                hasCustomPrices?.let {
                    if (hasCustomPrices) {
                        OrganisationDocument::prices ne null
                    } else {
                        OrganisationDocument::prices eq null
                    }
                }
            )
        )
    }

    private fun sort(): Bson {
        return orderBy(
            linkedMapOf(
                OrganisationDocument::accessExpiresOn to false,
                OrganisationDocument::name to true
            )
        )
    }

    private fun <T : Organisation> Iterable<OrganisationDocument>.convert(): List<T> {
        return this.map { document ->
            fromDocument(document).cast<T>()
        }
    }

    private fun <T : Organisation> OrganisationDocument.convert(): T {
        return fromDocument(this).cast()
    }

    private fun <T : Organisation> Organisation.cast(): T {
        @Suppress("UNCHECKED_CAST")
        return this as T
    }
}
