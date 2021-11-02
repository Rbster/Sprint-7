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

        connection.use { conn ->
            conn.autoCommit = false
            val statementGetAccount1 = conn.prepareStatement("SELECT amount, version FROM account1 WHERE id = $accountId1")
            val statementGetAccount2 = conn.prepareStatement("SELECT amount, version FROM account1 WHERE id = $accountId2")
            val fromRow: DataRow
            val toRow: DataRow


            statementGetAccount1.use { account1 ->
                account1.executeQuery().use {
                    fromRow = DataRow(
                        accountId1,
                        it.getInt("amount"),
                        it.getInt("version")
                    )
                }
            }

            statementGetAccount2.use { account2 ->
                account2.executeQuery().use {
                    toRow = DataRow(
                        accountId2,
                        it.getInt("amount"),
                        it.getInt("version")
                    )
                }
            }

            val takeMoneyStatement = conn.prepareStatement("UPDATE account1 SET amount = amount - $amount WHERE id = $accountId1")

        }
    }
}
