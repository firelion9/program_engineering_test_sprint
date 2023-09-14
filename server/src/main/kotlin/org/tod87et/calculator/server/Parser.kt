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

abstract class Token() {
    abstract val type: TokenType
}

enum class SignType {
    PLUS, MINUS, MULTIPLY, DIVIDE, POWER
}

enum class TokenType {
    NUMBER, SIGN, LEFT_BRACKET, RIGHT_BRACKET
}

public class TokenNumber(val number: Double): Token() {
    override val type = TokenType.NUMBER

    override fun toString() = "(${number.toString()})"
}

class TokenSign(val signType: SignType): Token() {
    override val type = TokenType.SIGN

    override fun toString() = "[${type.toString()}]"
}

class TokenLeftBracket(): Token() {
    override val type = TokenType.LEFT_BRACKET
    override fun toString() = "[(]"
}

class TokenRightBracket(): Token() {
    override val type = TokenType.RIGHT_BRACKET
    override fun toString() = "[)]"
}

fun toToken(c: Char): Token {
    return when (c) {
        '+' -> TokenSign(SignType.PLUS)
        '-' -> TokenSign(SignType.MINUS)
        '*' -> TokenSign(SignType.MULTIPLY)
        '/' -> TokenSign(SignType.DIVIDE)
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
                if (isSignOrBracket || c.isWhitespace()) {
                    if (word.isNotEmpty()) {
                        tokens.add(toTokenNumber(word.toString()))

                        word.clear()
                    }
                    wordContainsPoint = false

                    if (!c.isWhitespace())
                        tokens.add(toToken(c))

                    continue
                }

                val isPoint = c == '.'
                if (isPoint) {
                    if (wordContainsPoint)
                        throw BadNumber("Double point in number")

                    word.append(".")

                    wordContainsPoint = true

                    continue
                }

                val isDigit = c.isDigit()
                if (isDigit) {
                    word.append(c.toString())

                    continue
                }

                throw UnsupportedSymbolException("Unsupported character")

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
                when (token.type) {
                    TokenType.LEFT_BRACKET -> {
                        bracketCounter++
                        levelQueue.add(operationQueue)
                        operationQueue = mutableListOf()
                    }
                    TokenType.RIGHT_BRACKET -> {
                        bracketCounter--
                        if (bracketCounter < 0)
                            throw IncorrectBracketSequence("")
                        operationQueue.add(TokenNumber(foldOperationQueue(operationQueue)))
                    }
                    TokenType.SIGN -> {
                        operationQueue.add(token)
                    }
                    TokenType.NUMBER -> {
                        operationQueue.add(token)
                    }
                }
            }

            return foldOperationQueue(operationQueue)
        }
    }
}