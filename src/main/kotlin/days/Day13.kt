package days

import utils.cutAt
import utils.readInputLines
import utils.replaceCharAt
import utils.splitBy

object Day13 {
    fun part1(input: List<String>): Int =
        input.asSequence()
            .splitBy { it.isBlank() }
            .sumOf { it.findValueOfFirstReflection() }

    fun part2(input: List<String>): Int {
        val mirrorPatterns = input.splitBy { it.isBlank() }
        return mirrorPatterns
            .map { it.findValueOfFirstReflection() }
            .mapIndexed { index, oldValue ->
                mirrorPatterns[index]
                    .replacementSequence()
                    .firstNotNullOf { newPattern ->
                        newPattern.verticalReflectionIndexes().firstOrNull { it != oldValue }
                            ?: newPattern.horizontalReflectionIndexes().map { it * 100 }.firstOrNull { it != oldValue }
                    }
            }.sum()
    }

    private fun List<String>.findValueOfFirstReflection() =
        verticalReflectionIndexes().firstOrNull() ?: (100 * horizontalReflectionIndexes().first())

    private fun List<String>.verticalReflectionIndexes() =
        this.map { line ->
            (1..line.lastIndex)
                .filter {
                    val (first, second) = line.cutAt(it)
                    if (first.length > second.length) first.takeLast(second.length) == second.reversed()
                    else first == second.take(first.length).reversed()
                }
        }.commonSubElements()

    private fun List<String>.horizontalReflectionIndexes() =
        (1..lastIndex).filter {
            if (it > lastIndex / 2) subList(it, size) == subList(2 * it - size, it).asReversed()
            else subList(0, it) == subList(it, it * 2).asReversed()
        }

    private fun List<String>.replacementSequence(): Sequence<List<String>> =
        asSequence().flatMapIndexed { rowIndex: Int, row: String ->
            row.asSequence().mapIndexed { columnIndex, char ->
                val newChar = if (char == '.') '#' else '.'
                toMutableList().apply { this[rowIndex] = row.replaceCharAt(columnIndex, newChar) }
            }
        }

    private fun <T> Iterable<Iterable<T>>.commonSubElements(): List<T> =
        first().filter { element -> this.all { element in it } }
}

fun main() {
    val input = readInputLines("Day13")
    println(Day13.part1(input))
    println(Day13.part2(input))
}
