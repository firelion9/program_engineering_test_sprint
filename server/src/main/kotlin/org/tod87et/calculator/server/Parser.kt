package org.tod87et.calculator.server

import java.lang.StringBuilder

class EvalException(message: String): Exception(message)
open class ParserException(message: String): Exception(message)
class UnsupportedSymbolException(message: String): ParserException(message)
class BadNumber(message: String): ParserException(message)
class IncorrectBracketSequence(message: String): ParserException(message)

private fun isSign (c: String) : Boolean {
    return listOf("+", "-", "*", "/", "^").contains(c)
}

abstract class Token() {}

enum class SignType {
    PLUS, MINUS, MULTIPLICATION, DIVISION, POWER
}

class TokenNumber(val number: Double): Token() {
    override fun toString() = "($number)"
}

class TokenSign(val signType: SignType): Token() {
    override fun toString() = "[$signType]"
}

class TokenLeftBracket(): Token() {
    override fun toString() = "[(]"
}

class TokenRightBracket(): Token() {
    override fun toString() = "[)]"
}

fun toToken(c: Char): Token {
    return when (c) {
        '+' -> TokenSign(SignType.PLUS)
        '-' -> TokenSign(SignType.MINUS)
        '*' -> TokenSign(SignType.MULTIPLICATION)
        '/' -> TokenSign(SignType.DIVISION)
        '^' -> TokenSign(SignType.POWER)
        '(' -> TokenLeftBracket()
        ')' -> TokenRightBracket()
        else -> throw UnsupportedSymbolException(c.toString())
    }
}

fun toTokenNumber(word: String): TokenNumber {
    return TokenNumber(word.toDoubleOrNull() ?: throw UnsupportedSymbolException(word))
}


class Parser private constructor(formula: String) {

    companion object {

        // Split by first number, sign or bracket
        fun tokenize(s: String): List<Token> {
            val tokens = mutableListOf<Token>()

            val word = StringBuilder()
            var wordContainsPoint = false

            for (c in s) {

                val isSignOrBracket = isSign(c.toString()) || c == '(' || c == ')'
                val isPoint = c == '.'
                val isDigit = c.isDigit()

                when {
                    isSignOrBracket || c.isWhitespace() -> {
                        if (word.isNotEmpty()) {
                            tokens.add(toTokenNumber(word.toString()))

                            word.clear()
                        }
                        wordContainsPoint = false

                        if (!c.isWhitespace())
                            tokens.add(toToken(c))
                    }
                    isPoint -> {
                        if (wordContainsPoint)
                            throw BadNumber("Double point in number")

                        word.append(".")

                        wordContainsPoint = true
                    }
                    isDigit ->
                        word.append(c.toString())
                    else ->
                        throw UnsupportedSymbolException("Unsupported character")
                }
            }
            if (word.isNotEmpty())
                tokens.add(toTokenNumber(word.toString()))

            return tokens
        }

        private fun foldOperationQueue(operationQueue: List<Token>): Double {
            TODO()
        }

        fun eval(formula: String): Double {
            var bracketCounter = 0

            var operationQueue = mutableListOf<Token>()
            val levelQueue = mutableListOf<MutableList<Token>>()

            val tokens = tokenize(formula)

            for (token in tokens) {
                when (token) {
                    is TokenLeftBracket -> {
                        bracketCounter++
                        levelQueue.add(operationQueue)
                        operationQueue = mutableListOf()
                    }

                    is TokenRightBracket -> {
                        bracketCounter--
                        if (bracketCounter < 0)
                            throw IncorrectBracketSequence("")
                        operationQueue.add(TokenNumber(foldOperationQueue(operationQueue)))
                    }

                    is TokenSign -> {
                        operationQueue.add(token)
                    }

                    is TokenNumber -> {
                        operationQueue.add(token)
                    }
                }
            }

            return foldOperationQueue(operationQueue)
        }
    }
}