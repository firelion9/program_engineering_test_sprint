package org.tod87et.database

import java.time.LocalDateTime

data class FormulaEntry(val id: Int, val formula: String, val result: Double, val date: LocalDateTime)