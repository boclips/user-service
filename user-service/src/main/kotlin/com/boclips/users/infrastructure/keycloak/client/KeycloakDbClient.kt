package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.infrastructure.keycloak.KeycloakDbProperties
import mu.KLogging
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

class KeycloakDbClient(
    private val keycloakDbProxy: KeycloakDbProxy,
    private val keycloakDbProperties: KeycloakDbProperties
) {

    companion object : KLogging()

    fun getAllUserIds(): List<String> {
        var connection: Connection? = null
        var statement: Statement? = null
        try {
            keycloakDbProxy.setupProxy(keycloakDbProperties.connectionName, keycloakDbProperties.proxyPort)
            connection = getConnection()
            statement = connection.prepareStatement("SELECT id as userid from user_entity")
            statement.fetchSize = 1000

            val allUserIds = mutableListOf<String>()
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                allUserIds.add(resultSet.getString("userid"))
            }

            logger.info { "Found ${allUserIds.size} users" }

            return allUserIds
        } catch (e: SQLException) {
            logger.error { "SQL State: ${e.sqlState}, \n ${e.message}" }
            throw e
        } catch (e: Exception) {
            logger.error { "exception getting ids from Keycloak db" }
            throw e
        } finally {
            connection?.close()
            statement?.close()
            keycloakDbProxy.closeProxy()
        }
    }

    private fun getConnection(): Connection {
        val connection = DriverManager.getConnection(
            "jdbc:postgresql://localhost:${keycloakDbProperties.proxyPort}/${keycloakDbProperties.dbName}",
            keycloakDbProperties.username,
            keycloakDbProperties.password
        )
        connection.autoCommit = false
        return connection
    }
}
