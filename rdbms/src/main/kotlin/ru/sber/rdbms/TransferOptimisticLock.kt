package ru.sber.rdbms

import java.sql.DriverManager

class TransferOptimisticLock {
    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        val connection = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/db",
            "postgres",
            "postgres"
        )

        data class DataRow(val id: Long, var amount: Long, var version: Long)

        connection.use { conn ->
            conn.autoCommit = false

            val statementGetAccount1 = conn.prepareStatement("SELECT amount, version FROM account1 WHERE id = $accountId1")
            val statementGetAccount2 = conn.prepareStatement("SELECT amount, version FROM account1 WHERE id = $accountId2")

            val fromRow: DataRow
            val toRow: DataRow

            statementGetAccount1.use { account1 ->
                account1.executeQuery().use {
                    it.next()
                    fromRow = DataRow(
                        accountId1,
                        it.getLong("amount"),
                        it.getLong("version")
                    )
                }
            }
            statementGetAccount2.use { account2 ->
                account2.executeQuery().use {
                    it.next()
                    toRow = DataRow(
                        accountId2,
                        it.getLong("amount"),
                        it.getLong("version")
                    )
                }
            }

            if (fromRow.amount < amount) {
                throw DBException(DBExceptionValue.NOT_ENOUGH_MONEY)
            }

            val takeMoneyStatement = conn.prepareStatement(
                "UPDATE account1 SET amount = ${fromRow.amount - amount}, version = ${fromRow.version + 1} WHERE id = $accountId1 AND version = ${fromRow.version}"
            )
            val giveMoneyStatement = conn.prepareStatement(
                "UPDATE account1 SET amount = ${toRow.amount + amount}, version = ${toRow.version + 1} WHERE id = $accountId2 AND version = ${toRow.version}")

            try {
//                sleep(5000)
                takeMoneyStatement.use {
                    it.execute()
                    if (it.updateCount == 0) {
                        throw DBException(DBExceptionValue.WAS_MODIFIED)
                    }
                }
                giveMoneyStatement.use {
                    it.execute()
                    if (it.updateCount == 0) {
                        throw DBException(DBExceptionValue.WAS_MODIFIED)
                    }
                }
                conn.commit()
            } catch (e: DBException) {
                conn.rollback()
                throw DBException(DBExceptionValue.WAS_MODIFIED)
            }
        }
    }
}
