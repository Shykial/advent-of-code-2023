package days

import utils.readInputLines

object Day09 {
    fun part1(input: List<String>): Int =
        input.asSequence()
            .map(::parseLine)
            .sumOf { ints -> ints.differencesSequence().sumOf { it.last() } }

    fun part2(input: List<String>): Int =
        input.asSequence()
            .map(::parseLine)
            .sumOf { ints ->
                ints.differencesSequence()
                    .map { it.first() }
                    .toList()
                    .asReversed()
                    .reduce { acc, n -> n - acc }
            }

    private fun parseLine(line: String) = line.split(" ").map { it.toInt() }

    private fun List<Int>.differencesSequence() = generateSequence(this) {
        it.zipWithNext { first, second -> second - first }
    }.takeWhile { it.any { n -> n != 0 } }
}

fun main() {
    val input = readInputLines("Day09")
    println(Day09.part1(input))
    println(Day09.part2(input))
}
