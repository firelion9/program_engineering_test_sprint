package org.tod87et.calculator.server

class Parser private constructor(formula: String) {

    class EvalException(message: String): Exception(message)
    open class ParserException(message: String): Exception(message)
    class UnsupportedSymbolException(message: String): ParserException(message)
    class BadNumber(message: String): ParserException(message)

    companion object {

        private fun isSign (c: String) : Boolean {
            return listOf("+", "-", "*", "/", "^").contains(c)
        }

        // Split by first number, sign or bracket
        fun tokenize(s: String): List<String> {
            val tokens = mutableListOf<String>()

            var word = ""
            var wordContainsPoint = false

            for (c in s.withIndex()) {

                val isSignOrBracket = isSign(c.value.toString()) || c.value == '(' || c.value == ')'
                if (isSignOrBracket || c.value.isWhitespace()) {
                    if (word.isNotEmpty()) {
                        tokens.add(word)

                        word = ""
                    }
                    wordContainsPoint = false

                    if (!c.value.isWhitespace())
                        tokens.add(c.value.toString())

                    continue
                }

                val isPoint = c.value == '.'
                if (isPoint) {
                    if (wordContainsPoint)
                        throw BadNumber("Double point in number")

                    word += '.'

                    wordContainsPoint = true

                    continue
                }

                val isDigit = c.value.isDigit()
                if (isDigit) {
                    word += c.value

                    continue
                }

                throw UnsupportedSymbolException("Unsupported character")

            }
            if (word.isNotEmpty())
                tokens.add(word)

            return tokens
        }

        enum class Binop {
            PLUS, MINUS, MULTIPLY, DIVIDE, POWER
        }

        private fun foldOperationQueue(operationQueue: MutableList<Pair<Double, Binop>>, lastElement: Double): Double {
            TODO()
        }

        fun eval(formula: String): Double {
            var bracketCounter = 0

            var operationQueue = mutableListOf<Pair<Double, Binop>>()
            val levelQueue = mutableListOf<MutableList<Pair<Double, Binop>>>()

            var previousNumber = 0.0

            val tokens = tokenize(formula)

            for (word in tokens) {
                if (word == "(") {
                    bracketCounter++
                    levelQueue.add(operationQueue)
                    operationQueue = mutableListOf()
                } else if (word == ")") {
                    bracketCounter--
                    previousNumber = foldOperationQueue(operationQueue, previousNumber)
                } else if (isSign(word)) {
                    when (word) {
                        "+" -> operationQueue.add(Pair(previousNumber, Binop.PLUS))
                        "-" -> operationQueue . add (Pair(previousNumber, Binop.MINUS))
                        "*" -> operationQueue.add(Pair(previousNumber, Binop.MULTIPLY))
                        "/" -> operationQueue.add(Pair(previousNumber, Binop.DIVIDE))
                        "^" -> operationQueue.add(Pair(previousNumber, Binop.POWER))
                    }
                }
                else {
                    val numberOrNull = word.toDoubleOrNull()
                    previousNumber = numberOrNull ?: throw ParserException(word)
                }


            }

            previousNumber = foldOperationQueue(operationQueue, previousNumber)

            return previousNumber
        }
    }
}