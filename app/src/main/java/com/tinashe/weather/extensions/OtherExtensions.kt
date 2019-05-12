package com.tinashe.weather.extensions

fun String.containsEither(vararg others: String): Boolean {
    for (other in others) {
        if (this.contains(other, true)) {
            return true
        }
    }

    return false
}

fun Double.toFahrenheit(): Double {
    return ((this * 9) / 5) + 32
}