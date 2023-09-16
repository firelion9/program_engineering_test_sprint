package org.tod87et.calculator.server

import org.tod87et.calculator.server.Stack
import java.lang.StringBuilder
import kotlin.math.pow

class EvalException(message: String): Exception(message)
open class ParserException(message: String): Exception(message)
class UnsupportedSymbolException(message: String): ParserException(message)
class BadNumber(message: String): ParserException(message)
class IncorrectBracketSequence(message: String): ParserException(message)

typealias Stack<T> = MutableList<T>

private fun isSignOrBracket(c: Char): Boolean {
    return c in listOf('+', '-', '*', '/', '^', ')', '(')
}

interface Token

interface TokenOperation: Token

enum class SignType {
    PLUS, MINUS, MULTIPLICATION, DIVISION, POWER
}


class TokenNumber(val number: Double): Token {
    override fun toString() = "($number)"
}

class TokenSign(val signType: SignType): TokenOperation {

    val priority = when (this.signType) {
        SignType.PLUS -> 1
        SignType.MINUS -> 1
        SignType.MULTIPLICATION -> 2
        SignType.DIVISION -> 2
        SignType.POWER -> 3
    }

    override fun toString() = "[$signType]"

    fun calculate(b: Double, a: Double): Double {
        return when (this.signType) {
            SignType.PLUS -> a + b
            SignType.MINUS -> a - b
            SignType.MULTIPLICATION -> a * b
            SignType.DIVISION -> a / b
            SignType.POWER -> a.pow(b)
        }
    }
}

class TokenLeftBracket: TokenOperation {
    override fun toString() = "[(]"
}

class TokenRightBracket: TokenOperation {
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

                val isSignOrBracket = isSignOrBracket(c)
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
                    isDigit -> word.append(c.toString())
                    else -> throw UnsupportedSymbolException("Unsupported character")
                }
            }
            if (word.isNotEmpty())
                tokens.add(toTokenNumber(word.toString()))

            return tokens
        }


        private fun foldOperationQueue(tokens: List<Token>): Double {


            val operationStack: Stack<TokenSign> = mutableListOf()
            val valueStack : Stack<TokenNumber> = mutableListOf()

            for (token in tokens) {
                when (token) {
                    is TokenSign -> {
                        while (operationStack.isNotEmpty()) {
                            val next = operationStack.last()
                            if (next.priority < token.priority) break
                            operationStack.removeLast()
                            if (valueStack.count() < 2) throw EvalException("")
                            val calculationResult = next.calculate(
                                valueStack.removeLast().number,
                                valueStack.removeLast().number
                            )
                            valueStack.add(TokenNumber(calculationResult))
                        }

                        operationStack.add(token)
                    }

                    is TokenNumber -> valueStack.add(token)

                    else -> throw EvalException("")
                }
            }

            while (operationStack.isNotEmpty()) {
                val next = operationStack.removeLast()
                if (valueStack.count() < 2) throw EvalException("")
                val calculationResult = next.calculate(
                    valueStack.removeLast().number,
                    valueStack.removeLast().number
                )
                valueStack.add(TokenNumber(calculationResult))
            }

            if (valueStack.count() != 1) throw EvalException("")

            return valueStack[0].number
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
                        val foldResult = TokenNumber(foldOperationQueue(operationQueue))
                        operationQueue = levelQueue.removeLast()
                        operationQueue.add(foldResult)
                    }

                    is TokenSign -> operationQueue.add(token)

                    is TokenNumber -> operationQueue.add(token)
                }
            }

            return foldOperationQueue(operationQueue)
        }
    }
}