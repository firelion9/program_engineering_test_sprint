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
        fun lex(s: String): Pair<String, String> {
            var word = ""
            var wordContainsPoint = false

            for (c in s.withIndex()) {
                val isSignOrBracket = isSign(c.value.toString()) || c.value == '(' || c.value == ')'
                if (isSignOrBracket) {
                    return if (word.isEmpty())
                        Pair(
                            s.slice(c.index .. c.index),
                            s.slice(c.index+1 until s.length)
                        )
                    else
                        Pair(
                            word,
                            s.slice(c.index until s.length)
                        )
                }
                if (c.value.isWhitespace()) {
                    if (word.isEmpty()) continue
                    else return Pair(
                        word,
                        s.slice(c.index until s.length)
                    )
                }

                val isPoint = c.value == '.'

                if (isPoint) {
                    if (wordContainsPoint)
                        throw BadNumber("Double point in number")

                    word += '.'

                    wordContainsPoint = true
                }

                val isDigit = c.value.isDigit()

                if (isDigit) {
                    word += c.value
                }

                if (!isDigit && !isSignOrBracket && !isPoint)
                    throw UnsupportedSymbolException("Unsupported character")

            }
            return Pair(word, "")
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

            var currentFormula = formula

            do {
                val (word, otherFormula) = lex(currentFormula)
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

                currentFormula = otherFormula.trimStart(' ')

            } while (currentFormula.isNotEmpty())

            previousNumber = foldOperationQueue(operationQueue, previousNumber)

            return previousNumber
        }
    }
}