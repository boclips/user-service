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
            mongoTemplate.count(query(searchRequest = searchRequest), OrganisationDocument::class.java)

        val results = mongoTemplate.find(findQuery(searchRequest = searchRequest), OrganisationDocument::class.java)

        return PageImpl(results, PageRequest.of(page, pageSize), totalElements)
    }

    private fun findQuery(searchRequest: OrganisationSearchRequest): Query {
        val offset = searchRequest.page * searchRequest.size

        val query = query(searchRequest)
        query.limit(searchRequest.size)
        query.skip(offset.toLong())

        return query
    }

    private fun query(searchRequest: OrganisationSearchRequest): Query {
        val query = Query()

        searchRequest.name?.let {
            query.addCriteria(Criteria.where("name").`in`(it))
        }
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
}

