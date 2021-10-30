package ru.sber.rdbms

import java.sql.DriverManager

class TransferConstraint {
    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "postgres"
    )
    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        connection.use { conn ->
            val prepareStatement = conn.prepareStatement("select 1")
            prepareStatement.use { statement ->
                val resultSet = statement.executeQuery()
                resultSet.use {
                    println("Has result: ${it.next()}")
                    val result = it.getInt(1)
                    println("Execution result: $result")
                }
            }
        }
        TODO()
    }
}
