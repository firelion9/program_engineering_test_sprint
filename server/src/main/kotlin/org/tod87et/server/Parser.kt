package org.tod87et.server

class Parser private constructor(formula: String) {

    class EvalException(message: String): Exception(message)

    companion object {

        // Split by first number, sign or bracket
        fun lex(s: String): Pair<String, String> {
            var word = ""
            var wordContainsPoint = false

            for (c in s.withIndex()) {
                if (c.value.isWhitespace()) {
                    if (word.isEmpty()) continue
                    else return Pair(
                        word,
                        s.slice(c.index until s.length-1)
                    )
                }

                val isSignOrBracket = listOf('+', '-', '*', '/', '(', ')', '^').contains(c.value)
                if (word.isEmpty() && isSignOrBracket) {
                    return Pair(
                        s.slice(c.index .. c.index),
                        s.slice(c.index+1 until s.length)
                    )
                }

                val isPoint = c.value == '.'

                if (isPoint) {
                    if (wordContainsPoint)
                        throw EvalException("Unsupported number")

                    word += '.'

                    wordContainsPoint = true
                }

                val isDigit = c.value.isDigit()

                if (isDigit) {
                    word += c.value
                }

                if (!isDigit && !isSignOrBracket && !isPoint)
                    throw EvalException("Unsupported character")

            }
            return Pair(word, "")
        }
        fun eval(formula: String): Double {
            TODO()
        }
    }
}