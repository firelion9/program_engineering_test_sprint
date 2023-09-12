package org.server

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.tod87et.server.Parser

class ParserTest {

    @Test
    fun testLex() {
        assertEquals(Parser.lex("42"), Pair("42", ""))

        assertEquals(Parser.lex("42.11"), Pair("42.11", ""))

        assertEquals(Parser.lex("42 ("), Pair("42", " ("))

        assertEquals(Parser.lex("   +42 ("), Pair("+", "42 ("))

        assertEquals(Parser.lex("+98 + 9"), Pair("+", "98 + 9"))

        assertEquals(Parser.lex("+     98 + 9"), Pair("+", "     98 + 9"))
    }

    @Test
    fun testEval() {
        assertEquals(Parser.eval("42"), 42.0)
    }
}