package org.tod87et.calculator.server.database

import java.time.Instant

data class FormulaEntry(val id: Int, val formula: String, val result: Double, val date: Instant)