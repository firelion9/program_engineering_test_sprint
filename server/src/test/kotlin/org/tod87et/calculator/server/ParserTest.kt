package org.tod87et.calculator.server

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParserTest {

    @Test
    fun testTokenize() {
        assertEquals(listOf(TokenNumber(42.0)), Parser.tokenize("42"))

        assertEquals(listOf(TokenNumber(42.0), TokenNumber(54.0)), Parser.tokenize("42 54"))

        assertEquals(listOf(TokenNumber(42.11)), Parser.tokenize("42.11"),)

        assertEquals(listOf(TokenLeftBracket(), TokenNumber(5.0)), Parser.tokenize("(5"))

        assertEquals(listOf(TokenNumber(42.0), TokenLeftBracket()), Parser.tokenize("42("))

        assertEquals(listOf(TokenNumber(42.0), TokenRightBracket()), Parser.tokenize("42 )"))

        assertEquals(listOf(TokenSign(SignType.MINUS), TokenNumber(42.0), TokenLeftBracket()), Parser.tokenize("   -42 ("))

        assertEquals(
            listOf(TokenSign(SignType.MULTIPLICATION), TokenNumber(98.0), TokenSign(SignType.DIVISION), TokenNumber(9.0)),
            Parser.tokenize("*98 / 9")
        )

        assertEquals(
            listOf(TokenSign(SignType.PLUS), TokenNumber(98.0), TokenSign(SignType.POWER), TokenNumber(9.0)),
            Parser.tokenize("+     98 ^ 9")
        )
    }

    @Test
    fun testEval() {
        assertEquals(42.0, Parser.eval("42"))
        assertEquals(12.0, Parser.eval("5+7"))
        assertEquals(6.0, Parser.eval("2+2*2"))
        assertEquals(50.0, Parser.eval("5*(3+7)"))
        assertEquals(50.0, Parser.eval("(3+7)*5"))
        assertEquals(16.0, Parser.eval("2^2^2"))
        assertEquals(-404.0, Parser.eval("-404")) // unary minus
        assertEquals(-204.0, Parser.eval("(-404 + 200)"))
        assertEquals(-6.0, Parser.eval("(3 * -2)"))
        assertEquals(-30.0, Parser.eval("(3 * -(7 + 3)"))
        assertEquals(256.0, Parser.eval("2^2^3")) // power is right-associative
        assertEquals(134217728.0, Parser.eval("2^(1+2)^3"))
        assertEquals(18.0, Parser.eval("(6+2*7)-((3^2-1*2)+(7-2^2*3))"))
    }
}