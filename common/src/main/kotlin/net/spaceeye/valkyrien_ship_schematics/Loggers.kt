package net.spaceeye.valkyrien_ship_schematics

import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger("valkyrien_ship_schematics")

fun WLOG(s: String) = logger.warn(s)
fun DLOG(s: String) = logger.debug(s)
fun ELOG(s: String) = logger.error(s)