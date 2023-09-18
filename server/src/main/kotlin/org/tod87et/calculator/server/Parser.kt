package org.tod87et.calculator.server

import io.ktor.util.*
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

enum class SignType {
    PLUS, MINUS, MULTIPLICATION, DIVISION, POWER
}

data class TokenNumber(val number: Double): Token

data class TokenSign(val signType: SignType): Token {

    val priority = when (this.signType) {
        SignType.PLUS -> 1
        SignType.MINUS -> 1
        SignType.MULTIPLICATION -> 2
        SignType.DIVISION -> 2
        SignType.POWER -> 3
    }

    val isRightAssociative = when (this.signType) {
        SignType.POWER -> true
        else -> false
    }

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

class TokenLeftBracket: Token {
    override fun toString() = "[(]"
    override fun equals(other: Any?): Boolean = other is TokenLeftBracket
    override fun hashCode(): Int = javaClass.hashCode()
}

class TokenRightBracket: Token {
    override fun equals(other: Any?): Boolean = other is TokenRightBracket
    override fun hashCode(): Int = javaClass.hashCode()
}

fun toToken(c: Char): Token =
    when (c) {
        '+' -> TokenSign(SignType.PLUS)
        '-' -> TokenSign(SignType.MINUS)
        '*' -> TokenSign(SignType.MULTIPLICATION)
        '/' -> TokenSign(SignType.DIVISION)
        '^' -> TokenSign(SignType.POWER)
        '(' -> TokenLeftBracket()
        ')' -> TokenRightBracket()
        else -> throw UnsupportedSymbolException(c.toString())
    }

fun toTokenNumber(word: String): TokenNumber {
    return TokenNumber(word.toDoubleOrNull() ?: throw BadNumber("Incorrect number: $word"))
}


fun tokenize(s: String): List<Token> {
    val tokens = mutableListOf<Token>()

    val word = StringBuilder()

    for (c in s) {

        val isSignOrBracket = isSignOrBracket(c)
        val isDigit = c.isDigit() || c == '.' || c == 'e' || c == '-' && word.lastOrNull() == 'e'
        when {
            isDigit -> word.append(c)

            isSignOrBracket || c.isWhitespace() -> {
                if (word.isNotEmpty()) {
                    tokens.add(toTokenNumber(word.toString()))

                    word.clear()
                }

                if (!c.isWhitespace())
                    tokens.add(toToken(c))
            }
            else -> throw UnsupportedSymbolException("Unsupported character: $c")
        }
    }
    if (word.isNotEmpty())
        tokens.add(toTokenNumber(word.toString()))

    return tokens
}


private fun foldOperationQueue(tokens: List<Token>): Double {

    val operationStack: Stack<TokenSign> = mutableListOf()
    val valueStack : Stack<Double> = mutableListOf()

    var previousTokenIsNotNumber = true
    var previousIsUnaryMinus = false

    for (token in tokens) {
        when (token) {
            is TokenSign -> {
                val isUnaryMinus = token.signType == SignType.MINUS && previousTokenIsNotNumber
                if (isUnaryMinus) {
                    previousIsUnaryMinus = true
                    continue
                }

                while (operationStack.isNotEmpty()) {
                    val next = operationStack.last()
                    if (next.priority < token.priority || (next.signType == token.signType && next.isRightAssociative)) break
                    operationStack.removeLast()
                    if (valueStack.count() < 2) throw EvalException("Incorrect expression")
                    val calculationResult = next.calculate(
                        valueStack.removeLast(),
                        valueStack.removeLast()
                    )
                    valueStack.add(calculationResult)
                }

                operationStack.add(token)
                previousTokenIsNotNumber = true
                previousIsUnaryMinus = false
            }

            is TokenNumber -> {
                val number = token.number * if (previousIsUnaryMinus) -1 else 1
                valueStack.add(number)

                previousTokenIsNotNumber = false
            }

            else -> throw EvalException("Incorrect state")
        }
    }

    while (operationStack.isNotEmpty()) {
        val next = operationStack.removeLast()
        if (valueStack.count() < 2) throw EvalException("Incorrect expression")
        val calculationResult = next.calculate(
            valueStack.removeLast(),
            valueStack.removeLast()
        )
        valueStack.add(calculationResult)
    }

    if (valueStack.count() != 1) throw EvalException("Incorrect state")

    return valueStack.first()
}


fun eval(formula: String): Double {
    var bracketCounter = 0

    var operationQueue = mutableListOf<Token>()
    val levelQueue = mutableListOf<MutableList<Token>>()

    val tokens = tokenize(formula.toLowerCasePreservingASCIIRules())

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
                    throw IncorrectBracketSequence("IncorrectBracketSequence")
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