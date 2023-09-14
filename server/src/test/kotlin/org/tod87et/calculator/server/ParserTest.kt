package org.tod87et.calculator.server

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParserTest {

    fun assertToStringEquals(a: Any, b: Any) {
        assertEquals(a.toString(), b.toString())
    }
    @Test
    fun testTokenize() {
        assertToStringEquals(listOf(TokenNumber(42.0)), Parser.tokenize("42"))

        assertToStringEquals(listOf(TokenNumber(42.0), TokenNumber(54.0)), Parser.tokenize("42 54"))

        assertToStringEquals(listOf(TokenNumber(42.11)), Parser.tokenize("42.11"),)

        assertToStringEquals(listOf(TokenLeftBracket(), TokenNumber(5.0)), Parser.tokenize("(5"))

        assertToStringEquals(listOf(TokenNumber(42.0), TokenLeftBracket()), Parser.tokenize("42("))

        assertToStringEquals(listOf(TokenNumber(42.0), TokenRightBracket()), Parser.tokenize("42 )"))

        assertToStringEquals(listOf(TokenSign(SignType.MINUS), TokenNumber(42.0), TokenLeftBracket()), Parser.tokenize("   -42 ("))

        assertToStringEquals(listOf(TokenSign(SignType.MULTIPLY), TokenNumber(98.0), TokenSign(SignType.DIVIDE), TokenNumber(9.0)), Parser.tokenize("*98 / 9"))

        assertToStringEquals(listOf(TokenSign(SignType.PLUS), TokenNumber(98.0), TokenSign(SignType.POWER), TokenNumber(9.0)), Parser.tokenize("+     98 ^ 9"))
    }

    @Test
    fun testEval() {
        assertEquals(Parser.eval("42"), 42.0)
    }
}