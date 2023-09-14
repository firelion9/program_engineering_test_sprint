package org.tod87et.calculator.server

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParserTest {

    @Test
    fun testTokenize() {
        assertEquals(listOf("42"), Parser.tokenize("42"))

        assertEquals(listOf("42", "54"), Parser.tokenize("42 54"))

        assertEquals(listOf("42.11"), Parser.tokenize("42.11"),)

        assertEquals(listOf("(", "5"), Parser.tokenize("(5"))

        assertEquals(listOf("42", "("), Parser.tokenize("42("))

        assertEquals(listOf("42", "("), Parser.tokenize("42 ("))

        assertEquals(listOf("+", "42", "("), Parser.tokenize("   +42 ("))

        assertEquals(listOf("+", "98", "+", "9"), Parser.tokenize("+98 + 9"))

        assertEquals(listOf("+", "98", "+", "9"), Parser.tokenize("+     98 + 9"))
    }

    @Test
    fun testEval() {
        assertEquals(Parser.eval("42"), 42.0)
    }
}