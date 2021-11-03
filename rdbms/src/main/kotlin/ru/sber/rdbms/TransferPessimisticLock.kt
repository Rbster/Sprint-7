package ru.sber.rdbms

import java.sql.DriverManager

class TransferPessimisticLock {
    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        val connection = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/db",
            "postgres",
            "postgres"
        )

        data class DataRow(val id: Long, var amount: Int, var version: Int)

        val firstLockedId = if (accountId1 > accountId2) accountId2 else accountId1
        val secondLockedId = if (accountId1 > accountId2) accountId1 else accountId2

        connection.use { conn ->
            val statementGetAccount1 = conn.prepareStatement("SELECT amount FROM account1 WHERE id = $firstLockedId FOR UPDATE")
            val statementGetAccount2 = conn.prepareStatement("SELECT amount FROM account1 WHERE id = $secondLockedId FOR UPDATE")

            var fromAmount: Long
            var toAmount: Long

            statementGetAccount1.use { account1 ->
                account1.executeQuery().use {
                    it.next()
                    fromAmount = it.getLong("amount")

                }
            }

            statementGetAccount2.use { account2 ->
                account2.executeQuery().use {
                    it.next()
                    toAmount = it.getLong("amount")
                }
            }

            if (firstLockedId != accountId1) {
                fromAmount = toAmount.also { toAmount = fromAmount }
            }

            if (fromAmount < amount) {
                throw DBException(DBExceptionValue.NOT_ENOUGH_MONEY)
            }

            val takeMoneyStatement = conn.prepareStatement("UPDATE account1 SET amount = ${fromAmount - amount} WHERE id = $accountId1")
            val giveMoneyStatement = conn.prepareStatement("UPDATE account1 SET amount = ${toAmount + amount} WHERE id = $accountId2")

            takeMoneyStatement.use {
                it.execute()
            }
            giveMoneyStatement.use {
                it.execute()
            }
        }
    }
}
