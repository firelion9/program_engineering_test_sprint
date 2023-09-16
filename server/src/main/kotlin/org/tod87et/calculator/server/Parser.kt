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
    override fun toString() = "[$signType]"

    fun getDelegate(): (Double, Double) -> Double {
        return when (this.signType) {
            SignType.PLUS -> { b, a -> a + b }
            SignType.MINUS -> { b, a -> a - b }
            SignType.MULTIPLICATION -> { b, a -> a * b }
            SignType.DIVISION -> { b, a -> a / b }
            SignType.POWER -> { b, a -> a.pow(b) }
        }
    }

    fun getPriority(): Int {
        return when (this.signType) {
            SignType.PLUS -> 1
            SignType.MINUS -> 1
            SignType.MULTIPLICATION -> 2
            SignType.DIVISION -> 2
            SignType.POWER -> 3
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
/*

        private fun foldOperationQueue(operationQueue: List<Token>): Double {

            var rightOperand = mutableListOf<Token>()
            var leftOperand =  mutableListOf<Token>()

            var highestPrecedence = 0
            var currentOperation: SignType

            for (token in operationQueue)
            {
                if (token is TokenNumber)
                {
                    if (highestPrecedence == 0) leftOperand.add(token)
                    else rightOperand.add(token)
                }
            }
        }
*/

        fun eval(formula: String): Double {

            val operationStack: Stack<TokenOperation> = mutableListOf()
            val valueStack : Stack<TokenNumber> = mutableListOf()

            val tokens = tokenize(formula)

            for (token in tokens) {
                when (token) {
                    is TokenLeftBracket -> {
                        operationStack.add(token)
                    }

                    is TokenRightBracket -> {
                        while (operationStack.lastOrNull() !is TokenLeftBracket){
                            if (operationStack.isEmpty()) throw IncorrectBracketSequence("")
                            if (valueStack.count() < 2) throw EvalException("")
                            valueStack.add(TokenNumber((operationStack.removeLast() as TokenSign).getDelegate()
                                (valueStack.removeLast().number, valueStack.removeLast().number)))
                        }
                        operationStack.removeLast()
                    }

                    is TokenSign -> {
                        while (operationStack.isNotEmpty()) {
                            val next = operationStack.last()
                            if (next is TokenLeftBracket) break
                            if ((next as TokenSign).getPriority() < token.getPriority()) break
                            operationStack.removeLast()
                            if (valueStack.count() < 2) throw EvalException("")
                            valueStack.add(TokenNumber((next as TokenSign).getDelegate()
                                (valueStack.removeLast().number, valueStack.removeLast().number)))
                        }

                        operationStack.add(token)
                    }

                    is TokenNumber -> valueStack.add(token)
                }
            }

            while (operationStack.isNotEmpty()) {
                val next = operationStack.removeLast()
                if (next is TokenLeftBracket) throw IncorrectBracketSequence("")
                if (valueStack.count() < 2) throw EvalException("")
                valueStack.add(TokenNumber((next as TokenSign).getDelegate()
                    (valueStack.removeLast().number, valueStack.removeLast().number)))
            }

            if (valueStack.count() != 1) throw EvalException("")

            return valueStack[0].number
        }
    }
}