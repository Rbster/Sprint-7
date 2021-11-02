package ru.sber.rdbms

import java.lang.Thread.sleep
import java.sql.DriverManager

class TransferOptimisticLock {
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
                    it.next()
                    fromRow = DataRow(
                        accountId1,
                        it.getInt("amount"),
                        it.getInt("version")
                    )
                }
            }
            print("kik")
            statementGetAccount2.use { account2 ->
                account2.executeQuery().use {
                    it.next()
                    toRow = DataRow(
                        accountId2,
                        it.getInt("amount"),
                        it.getInt("version")
                    )
                }
            }

            if (fromRow.amount < amount) {
                throw Exception("Not enough money")
            }

            val takeMoneyStatement = conn.prepareStatement(
                "UPDATE account1 SET amount = amount - $amount, version = version + 1 WHERE id = $accountId1 AND version = ${fromRow.version}"
            )
            val giveMoneyStatement = conn.prepareStatement(
                "UPDATE account1 SET amount = amount + $amount, version = version + 1 WHERE id = $accountId2 AND version = ${toRow.version}")

            try {
                sleep(5000)
                takeMoneyStatement.use {
                    it.execute()
                    if (it.updateCount == 0) {
                        throw Exception("nooo")
                    }
                }
                giveMoneyStatement.use {
                    it.execute()
                    if (it.updateCount == 0) {
                        throw Exception("nooo")
                    }
                }
                conn.commit()
            } catch (e: Exception) {
                conn.rollback()
                throw Exception("there was update")
            }
        }
    }
}
