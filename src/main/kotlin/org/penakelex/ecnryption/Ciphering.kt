package org.penakelex.ecnryption

import kotlin.math.pow

private const val Alphabet = "HOD0hk964TdpJG2obPycLr7UqSNaAMjIleZzwngFsK1BXtvuQViCf3xE8R5WmY"
private const val Base: Byte = 62
private const val Seed: Short = 4689
private const val Spliterator = '.'
fun Any.cipher(addSalt: Boolean = false): String = buildString {
    var thisString = if (this@cipher is String) this@cipher else this@cipher.toString()
    if (addSalt) thisString = thisString.plus(getRandomString())
    for ((index, character) in thisString.withIndex()) {
        append(character.toCode())
        if (index != thisString.lastIndex) append(Spliterator)
    }
}

private fun Char.toCode(): String = buildString {
    var code = code * Seed
    while (code != 0) {
        append(Alphabet[code.rem(Base)])
        code = code.div(Base)
    }
}

private fun getRandomString(): String {
    val characters = ('A'..'Z').plus('a'..'z').plus('0'..'9')
    return buildString {
        repeat((Math.random() * 20).toInt()) {
            append(characters.random())
        }
    }
}

fun String.unCipher(): String {
    if (isEmpty()) return ""
    val symbolsIndices = buildMap { for ((index, symbol) in Alphabet.withIndex()) set(symbol, index) }
    return buildString {
        for (code in this@unCipher.split(Spliterator)) {
            append(code.unCode(symbolsIndices))
        }
    }
}

private fun String.unCode(symbolsIndices: Map<Char, Int>): Char {
    var power = 0
    var code = 0
    for (character in iterator()) {
        code = code.plus(symbolsIndices[character]!!.times(Base.toDouble().pow(power++).toInt()))
    }
    return code.div(Seed).toChar()
}