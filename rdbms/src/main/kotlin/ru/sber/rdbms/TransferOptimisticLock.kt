package ru.sber.rdbms

import java.sql.DriverManager

class TransferOptimisticLock {
    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
//        TODO()
        val connection = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/db",
            "postgres",
            "postgres"
        )
        connection.use { conn ->
            conn.autoCommit = false
            val statement = conn.prepareStatement("SELECT amount, version FROM account1 WHERE id = accountId1 FOR CHANGE")
        }
    }
}
