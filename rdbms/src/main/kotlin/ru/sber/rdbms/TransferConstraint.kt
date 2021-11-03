package ru.sber.rdbms

import java.sql.DriverManager

class TransferConstraint {

    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        val connection = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/db",
            "postgres",
            "postgres"
        )
        connection.use { conn ->
            val takeMoneyStatement = conn.prepareStatement(
                "UPDATE account1 SET amount = amount - $amount WHERE id = $accountId1"
            )
            val giveMoneyStatement = conn.prepareStatement(
                "UPDATE account1 SET amount = amount + $amount WHERE id = $accountId2"
            )

            try {
                takeMoneyStatement.use {
                    it.execute()
                }
                giveMoneyStatement.use {
                    it.execute()
                }
            } catch (e: Exception) {
                throw DBException(DBExceptionValue.NOT_ENOUGH_MONEY)
            }
        }
    }
}
