package com.boclips.users.infrastructure.organisation

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class CustomizedOrganisationRepositoryImpl(private val mongoTemplate: MongoTemplate) :
    CustomizedOrganisationRepository {
    override fun findOrganisations(searchRequest: OrganisationSearchRequest): Page<OrganisationDocument> {
        val page = searchRequest.page
        val pageSize = searchRequest.size

        val totalElements =
            mongoTemplate.count(countQuery(searchRequest = searchRequest), OrganisationDocument::class.java)

        val results = mongoTemplate.find(findQuery(searchRequest = searchRequest), OrganisationDocument::class.java)

        return PageImpl(results, PageRequest.of(page, pageSize), totalElements)
    }

    private fun countQuery(searchRequest: OrganisationSearchRequest): Query {
        val query = Query()

        searchRequest.countryCode?.let {
            query.addCriteria(Criteria.where("country.code").`is`(it))
        }
        searchRequest.organisationTypes?.let {
            query.addCriteria(Criteria.where("type").`in`(it))
        }
        searchRequest.parentOnly.let {
            query.addCriteria(Criteria.where("parentOrganisation").`is`(null))
        }

        query.with(Sort.by(Sort.Direction.DESC, "accessExpiresOn").and(Sort.by(Sort.Direction.ASC, "name")))
        return query
    }

    private fun findQuery(searchRequest: OrganisationSearchRequest): Query {
        val query = countQuery(searchRequest)

        val offset = searchRequest.page * searchRequest.size
        query.limit(searchRequest.size)
        query.skip(offset.toLong())

        return query
    }
}

