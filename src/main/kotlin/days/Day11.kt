package days

import utils.LongCoordinates
import utils.readInputLines
import kotlin.math.abs

object Day11 {
    fun part1(input: List<String>): Long = solve(input, expansionSize = 1)

    fun part2(input: List<String>): Long = solve(input, expansionSize = 999999)

    private fun solve(input: List<String>, expansionSize: Long) =
        parseAndFindGalaxiesCoordinatesAfterExpansion(input, expansionSize)
            .generatePairsSequence()
            .sumOf { (first, second) -> abs(second.y - first.y) + abs(second.x - first.x) }

    private fun parseAndFindGalaxiesCoordinatesAfterExpansion(
        imageLines: List<String>,
        expansionSize: Long
    ): List<LongCoordinates> {
        val rowsWithNoGalaxies = imageLines.mapIndexedNotNull { index, row ->
            index.takeIf { '#' !in row }
        }
        val columnsWithNoGalaxies = imageLines.first().indices.filter { column ->
            imageLines.none { it[column] == '#' }
        }

        return buildList {
            var rowExpansionSize = 0L
            imageLines.forEachIndexed { rowIndex, line ->
                if (rowIndex in rowsWithNoGalaxies) rowExpansionSize += expansionSize
                var columnExpansionSize = 0L
                line.forEachIndexed { columnIndex, char ->
                    if (columnIndex in columnsWithNoGalaxies) columnExpansionSize += expansionSize
                    if (char == '#') add(
                        LongCoordinates(y = rowIndex + rowExpansionSize, x = columnIndex + columnExpansionSize)
                    )
                }
            }
        }
    }

    private fun <T> List<T>.generatePairsSequence(): Sequence<Pair<T, T>> = sequence {
        val elementsLeft = toMutableList()
        while (elementsLeft.isNotEmpty()) {
            val removed = elementsLeft.removeLast()
            elementsLeft.forEach { yield(removed to it) }
        }
    }
}

fun main() {
    val input = readInputLines("Day11")
    println(Day11.part1(input))
    println(Day11.part2(input))
}