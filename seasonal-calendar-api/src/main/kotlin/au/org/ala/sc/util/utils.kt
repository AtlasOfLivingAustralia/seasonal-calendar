package au.org.ala.sc.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val HTTP_NOT_FOUND = 404

inline fun <reified T> T.logger(): Logger {
    val baseClass = T::class.java
    val logClass = if (baseClass.simpleName == "Companion") baseClass.enclosingClass ?: baseClass else baseClass
    return LoggerFactory.getLogger(logClass)!!
}

private val uuidv1 = Regex("^[0-9A-F]{8}-[0-9A-F]{4}-[1][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}\$", RegexOption.IGNORE_CASE)
private val uuidv2 = Regex("^[0-9A-F]{8}-[0-9A-F]{4}-[2][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}\$", RegexOption.IGNORE_CASE)
private val uuidv3 = Regex("^[0-9A-F]{8}-[0-9A-F]{4}-[3][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}\$", RegexOption.IGNORE_CASE)
private val uuidv4 = Regex("^[0-9A-F]{8}-[0-9A-F]{4}-[4][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}\$", RegexOption.IGNORE_CASE)
private val uuidv5 = Regex("^[0-9A-F]{8}-[0-9A-F]{4}-[5][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}\$", RegexOption.IGNORE_CASE)
private val uuids = listOf(uuidv4, uuidv1, uuidv2, uuidv3, uuidv5)

fun String?.isUuid() = this?.trim()?.let { v -> uuids.any { uuid -> uuid.matches(v) } } ?: false
