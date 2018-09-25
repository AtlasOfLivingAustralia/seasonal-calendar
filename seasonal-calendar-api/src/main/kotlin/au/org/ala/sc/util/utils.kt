package au.org.ala.sc.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> T.logger(): Logger {
    val baseClass = T::class.java
    val logClass = if (baseClass.simpleName == "Companion") baseClass.enclosingClass ?: baseClass else baseClass
    return LoggerFactory.getLogger(logClass)!!
}

const val HTTP_NOT_FOUND = 404