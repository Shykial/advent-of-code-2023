package days

import utils.readInputLines

object Day01 {
    private val stringDigitMappings = mapOf(
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9",
    ) + (1..9).associate { it.toString() to it.toString() }
    private val digitsAsLettersRegex = Regex("""(?=(${stringDigitMappings.keys.joinToString("|")}))""")

    fun part1(input: List<String>): Int =
        input.sumOf { line -> "${line.first { it.isDigit() }}${line.last { it.isDigit() }}".toInt() }

    fun part2(input: List<String>): Int =
        input.asSequence()
            .map { line -> digitsAsLettersRegex.findAll(line).map { it.groupValues[1] } }
            .map { values -> stringDigitMappings[values.first()]!! + stringDigitMappings[values.last()]!! }
            .sumOf { it.toInt() }
}

fun main() {
    val input = readInputLines("Day01")
    println(Day01.part1(input))
    println(Day01.part2(input))
}
