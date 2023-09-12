package org.tod87et
class Parser private constructor(formula: String) {

    enum class Binop {
        PLUS, MINUS, MULTIPLY, DIVIDE
    }

    class EvalException(message: String): Exception(message)

    companion object {

        // Split by first number, sign or bracket
        fun lex(s: String): Pair<String, String> {
            TODO()
        }
        fun eval(formula: String): Double {
            TODO()
        }
    }
}