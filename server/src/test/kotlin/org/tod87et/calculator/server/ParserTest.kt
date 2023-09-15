package org.tod87et.calculator.server

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ParserTest {

    private fun assertToStringEquals(a: Any, b: Any) {
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

        assertToStringEquals(listOf(TokenSign(SignType.MULTIPLICATION), TokenNumber(98.0), TokenSign(SignType.DIVISION), TokenNumber(9.0)), Parser.tokenize("*98 / 9"))

        assertToStringEquals(listOf(TokenSign(SignType.PLUS), TokenNumber(98.0), TokenSign(SignType.POWER), TokenNumber(9.0)), Parser.tokenize("+     98 ^ 9"))
    }

    @Test
    fun testEval() {
        assertEquals(Parser.eval("42"), 42.0)
        assertEquals(Parser.eval("5+7"), 12.0)
        assertEquals(Parser.eval("2+2*2"), 6.0)
        assertEquals(Parser.eval("5*(3+7)"), 50.0)
        assertEquals(Parser.eval("(3+7)*5"), 50.0)
        assertEquals(Parser.eval("2^2^2"), 16.0)
        assertEquals(Parser.eval("-404"), -404.0) // unary minus
        assertEquals(Parser.eval("(-404 + 200)"), -204.0)
        assertEquals(Parser.eval("(3 * -2)"), -6.0)
        assertEquals(Parser.eval("2^2^3"), 256.0) // power is right-associative
        assertEquals(Parser.eval("2^(1+2)^3"), 134217728.0)
        assertEquals(Parser.eval("(6+2*7)-((3^2-1*2)+(7-2^2*3))"), 18.0)
    }
}