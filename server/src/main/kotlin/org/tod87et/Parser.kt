package org.tod87et
class Parser private constructor(formula: String) {

    enum class CharType {
        SIGN, DIGIT, BRACKET, NONE
    }

    class EvalException(message: String): Exception(message)

    companion object {

        // Split by first number, sign or bracket
        fun lex(s: String): Pair<String, String> {
            var word: String = ""
            var wordContainsPoint = false

            for (c in s.withIndex()) {
                if (c.value == ' ') {
                    if (word.isEmpty()) continue;
                    else return Pair(
                        word,
                        s.slice(c.index .. s.length-1))
                }

                val isSignOrBracket = listOf('+', '-', '*', '/', '(', ')', '^').contains(c.value)
                if (word.isEmpty() && isSignOrBracket) {
                    return Pair(
                        s.slice(c.index .. c.index),
                        s.slice(c.index+1 .. s.length-1))
                }

                val isPoint = c.value == '.'

                if (isPoint) {
                    if (wordContainsPoint)
                        throw EvalException("Unsupported number")

                    word += '.'

                    wordContainsPoint = true
                }

                val isDigit = ('0'..'9').contains(c.value)

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