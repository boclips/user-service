package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.Page
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationDetails
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.service.OrganisationDomainOnUpdate
import com.boclips.users.domain.service.OrganisationExpiresOnUpdate
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.domain.service.OrganisationTypeUpdate
import com.boclips.users.domain.service.OrganisationUpdate
import com.boclips.users.infrastructure.MongoDatabase
import com.boclips.users.infrastructure.organisation.OrganisationDocumentConverter.fromDocument
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.litote.kmongo.`in`
import org.litote.kmongo.and
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.orderBy
import org.litote.kmongo.regex
import org.litote.kmongo.save
import org.litote.kmongo.setValue
import org.litote.kmongo.util.idValue
import java.util.regex.Pattern

class MongoOrganisationRepository(
    private val mongoClient: MongoClient
) : OrganisationRepository {

    fun collection(): MongoCollection<OrganisationDocument> {
        return mongoClient.getDatabase(MongoDatabase.DB_NAME).getCollection<OrganisationDocument>(
            "organisations"
        )
    }

    override fun lookupSchools(
        schoolName: String,
        countryCode: String
    ): List<LookupEntry> {
        return collection()
            .find(
                and(
                    OrganisationDocument::country / LocationDocument::code eq countryCode,
                    OrganisationDocument::name.regex(Pattern.quote(schoolName), "i"),
                    OrganisationDocument::type eq OrganisationType.SCHOOL
                )
            )
            .toList().map { LookupEntry(it._id!!.toHexString(), it.name) }
    }

    override fun findApiIntegrationByRole(role: String): Organisation<ApiIntegration>? {
        return collection()
            .findOne(
                and(
                    OrganisationDocument::role eq role,
                    OrganisationDocument::type eq OrganisationType.API
                )
            )?.fetchParentAndConvert()
    }

    override fun <T : OrganisationDetails> save(organisation: Organisation<T>): Organisation<T> {
        return save(OrganisationDocumentConverter.toDocument(organisation).organisation).cast()
    }

    private fun save(organisationDocument: OrganisationDocument): Organisation<*> {
        collection().save(organisationDocument)
        collection().updateMany(
            OrganisationDocument::parent / OrganisationDocument::_id eq organisationDocument._id,
            setValue(OrganisationDocument::parent, organisationDocument)
        )
        return findOrganisationById(OrganisationId(organisationDocument._id!!.toHexString()))!!
    }

    override fun updateOne(update: OrganisationUpdate): Organisation<*>? {
        val document = collection().findOneById(ObjectId(update.id.value)) ?: return null

        val updatedDocument = when (update) {
            is OrganisationTypeUpdate -> document.copy(dealType = update.type)
            is OrganisationExpiresOnUpdate -> document.copy(accessExpiresOn = update.accessExpiresOn.toInstant())
            is OrganisationDomainOnUpdate -> document.copy(domain = update.domain)
        }

        return save(updatedDocument)
    }

    override fun updateOne(id: OrganisationId, updates: List<OrganisationUpdate>): Organisation<*>? {
        val document = collection().findOneById(ObjectId(id.value)) ?: return null

        val updatedDocument = updates.fold(document, { accumulator: OrganisationDocument, update: OrganisationUpdate ->
            return@fold when (update) {
                is OrganisationTypeUpdate -> accumulator.copy(dealType = update.type)
                is OrganisationExpiresOnUpdate -> accumulator.copy(accessExpiresOn = update.accessExpiresOn.toInstant())
                is OrganisationDomainOnUpdate -> accumulator.copy(domain = update.domain)
            }
        })

        return save(updatedDocument)
    }

    override fun findOrganisationsByParentId(parentId: OrganisationId): List<Organisation<*>> {
        return collection().find(BasicDBObject().append("parentOrganisation.\$id", ObjectId(parentId.value)))
            .fetchParentsAndConvert<OrganisationDetails>()
    }

    override fun findOrganisationById(id: OrganisationId): Organisation<*>? {
        return collection().findOneById(ObjectId(id.value))
            ?.fetchParentAndConvert<OrganisationDetails>()
    }

    override fun findSchoolById(id: OrganisationId): Organisation<School>? {
        return findOrganisationById(id)
            ?.takeIf { it.details is School }?.cast()
    }

    override fun findSchools(): List<Organisation<School>> {
        return collection().find(OrganisationDocument::type eq OrganisationType.SCHOOL).fetchParentsAndConvert()
    }

    override fun findOrganisations(
        name: String?,
        countryCode: String?,
        types: List<OrganisationType>?,
        page: Int,
        size: Int
    ): Page<Organisation<*>> {

        val filter = query(
            name = name,
            countryCode = countryCode,
            types = types
        )

        val totalElements = collection().countDocuments(filter)

        val results = collection()
            .find(filter)
            .sort(sort())
            .skip(page * size)
            .limit(size)
            .fetchParentsAndConvert<OrganisationDetails>()

        return Page(
            items = results,
            pageSize = size,
            pageNumber = page,
            totalElements = totalElements
        )
    }

    override fun findApiIntegrationByName(name: String): Organisation<ApiIntegration>? {
        return collection().findOne(
            and(
                OrganisationDocument::name eq name,
                OrganisationDocument::type eq OrganisationType.API
            )
        )
            ?.fetchParentAndConvert()
    }

    override fun findOrganisationByExternalId(id: String): Organisation<*>? {
        return collection().findOne(OrganisationDocument::externalId eq id)
            ?.fetchParentAndConvert<OrganisationDetails>()
    }

    private fun query(
        name: String?,
        countryCode: String?,
        types: List<OrganisationType>?
    ): Bson {

        return and(listOfNotNull(
            name?.let {
                OrganisationDocument::name.regex(".*$it.*", "i")
            },
            countryCode?.let {
                OrganisationDocument::country / LocationDocument::code eq it
            },
            types?.let {
                OrganisationDocument::type `in` it
            },
            OrganisationDocument::parentOrganisation eq null
        ))
    }

    private fun sort(): Bson {
        return orderBy(
            linkedMapOf(
                OrganisationDocument::accessExpiresOn to false,
                OrganisationDocument::name to true
            )
        )
    }

    private fun <T : OrganisationDetails> Iterable<OrganisationDocument>.fetchParentsAndConvert(): List<Organisation<T>> {

        val existingDocumentById: Map<ObjectId, OrganisationDocument> = this.associateBy { it._id!! }
        val allParentIds = this
            .mapNotNull { it.parentOrganisation?.id as ObjectId? }
        val idsOfParentsToFetch = allParentIds.toSet() - existingDocumentById.keys.toSet()

        val fetchedDocumentById =
            collection().find(OrganisationDocument::_id `in` idsOfParentsToFetch).associateBy { it._id!! }

        val parentDocumentById = existingDocumentById + fetchedDocumentById

        @Suppress("UNCHECKED_CAST")
        return this.map { document ->
            fromDocument(
                document,
                document.parentOrganisation?.id?.let { parentId -> parentDocumentById[parentId] }
            ) as Organisation<T>
        }
    }

    private fun <T : OrganisationDetails> OrganisationDocument.fetchParentAndConvert(): Organisation<T> {
        return listOf(this).fetchParentsAndConvert<T>().first()
    }

    private fun <T : OrganisationDetails> Organisation<*>.cast(): Organisation<T> {
        @Suppress("UNCHECKED_CAST")
        return this as Organisation<T>
    }
}
