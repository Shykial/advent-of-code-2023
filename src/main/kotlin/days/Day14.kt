package days

import utils.onNull
import utils.readInputLines

object Day14 {
    private val tiltRightLinesCache = mutableMapOf<String, String>()

    fun part1(input: List<String>): Int =
        input.turnRight()
            .flatMap { line -> countSoftRocksBeforeHardRock(line) }
            .sumOf { it.count * (2 * (it.indexOfStoppingHardRock + 1) - it.count - 1) / 2 }

    fun part2(input: List<String>): Int {
        val seenParabolicPlanes = linkedSetOf<List<String>>()
        val cycleOffset = generateSequence(input) { it.turnRight().tiltRight() }
            .drop(1)
            .filterIndexed { index, _ -> index % 4 == 3 }
            .firstNotNullOf { element ->
                seenParabolicPlanes.indexOf(element)
                    .takeIf { it != -1 }
                    .onNull { seenParabolicPlanes += element }
            }
        val cycleSize = seenParabolicPlanes.size - cycleOffset
        val indexOfElementInCycle = (1000000000 - cycleSize - 1) % cycleSize
        return seenParabolicPlanes.elementAt(cycleOffset + indexOfElementInCycle).countPoints()
    }

    private fun countSoftRocksBeforeHardRock(line: String) = buildList {
        var softRocksCount = 0
        line.forEachIndexed { index, char ->
            when (char) {
                'O' -> softRocksCount++
                '#' -> {
                    if (softRocksCount > 0) this += SoftRocksMapping(softRocksCount, index)
                    softRocksCount = 0
                }
            }
        }
        if (softRocksCount > 0) this += SoftRocksMapping(softRocksCount, line.length)
    }


    private fun List<String>.tiltRight() = map { line ->
        tiltRightLinesCache.getOrPut(line) { line.tiltRight() }
    }

    private fun String.tiltRight() = buildString(length) {
        var softRocksCount = 0
        var lastWriteIndex = -1
        this@tiltRight.forEachIndexed { index, char ->
            when (char) {
                'O' -> softRocksCount++
                '#' -> {
                    repeat(index - lastWriteIndex - softRocksCount - 1) { append('.') }
                    repeat(softRocksCount) { append('O') }
                    append('#')
                    softRocksCount = 0
                    lastWriteIndex = index
                }
            }
        }
        repeat(this@tiltRight.lastIndex - lastWriteIndex - softRocksCount) { append('.') }
        repeat(softRocksCount) { append('O') }
    }

    private fun List<String>.countPoints() = asReversed().withIndex().sumOf { (index, element) ->
        (index + 1) * element.count { it == 'O' }
    }

    private fun List<String>.turnRight() = mapIndexed { rowIndex, string ->
        buildString(string.length) {
            string.indices.reversed().forEach { columnIndex -> append(this@turnRight[columnIndex][rowIndex]) }
        }
    }

    private data class SoftRocksMapping(val count: Int, val indexOfStoppingHardRock: Int)
}

fun main() {
    val input = readInputLines("Day14")
    println(Day14.part1(input))
    println(Day14.part2(input))
}
