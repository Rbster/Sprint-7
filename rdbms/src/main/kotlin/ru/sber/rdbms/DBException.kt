package ru.sber.rdbms

class DBException(private val enum: DBExceptionValue) : Exception(enum.msg)


enum class DBExceptionValue(val msg: String) {
    NOT_ENOUGH_MONEY("Not enough money"),
    WAS_MODIFIED("The row was updated")
}