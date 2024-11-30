package days

import utils.readInputLines
import utils.repeat

object Day12 {
    private const val OPERATIONAL = '.'
    private const val DAMAGED = '#'
    private const val UNKNOWN = '?'

    fun part1(input: List<String>): Long =
        input
            .map { parseLine(it) }
            .sumOf { it.countPossibleArrangements() }

    fun part2(input: List<String>): Long =
        input
            .map { parseLinePart2(it) }
            .sumOf { it.countPossibleArrangements() }

    private val cachedArrangementCounts = HashMap<InputLine, Long>()

    private fun InputLine.countPossibleArrangements(): Long = cachedArrangementCounts.getOrPut(this) {
        when {
            expectedDamagedCounts.isEmpty() -> return@getOrPut if (DAMAGED in line) 0 else 1
            line.isEmpty() -> return@getOrPut 0
        }
        val first = line.first()
        var result = 0L
        val firstDamagedCount = expectedDamagedCounts.first()
        if (
            line.length >= firstDamagedCount
            && OPERATIONAL !in line.take(firstDamagedCount)
            && line.getOrNull(firstDamagedCount) != DAMAGED
        ) {
            result += InputLine(
                line = line.drop(firstDamagedCount + 1).dropOperational(),
                expectedDamagedCounts = expectedDamagedCounts.drop(1)
            ).countPossibleArrangements()
        }
        if (first == UNKNOWN) {
            result += copy(line = line.drop(1).dropOperational()).countPossibleArrangements()
        }
        result
    }

    private data class InputLine(
        val line: String,
        val expectedDamagedCounts: List<Int>,
    )

    private fun parseLine(input: String): InputLine {
        val (springsString, damagedCountsString) = input.split(" ")
        return InputLine(
            line = springsString.trimOperational(),
            expectedDamagedCounts = damagedCountsString.split(',').map { it.toInt() },
        )
    }

    private fun parseLinePart2(input: String): InputLine {
        val (springsString, damagedCountsString) = input.split(" ")
        return InputLine(
            line = springsString.repeat(5, separator = UNKNOWN).trimOperational(),
            expectedDamagedCounts = damagedCountsString.split(',').map { it.toInt() }.repeat(5),
        )
    }

    private fun String.trimOperational() = dropWhile { it == OPERATIONAL }.dropLastWhile { it == OPERATIONAL }
    private fun String.dropOperational() = dropWhile { it == OPERATIONAL }
}

fun main() {
    val input = readInputLines("Day12")
    println(Day12.part1(input))
    println(Day12.part2(input))
}