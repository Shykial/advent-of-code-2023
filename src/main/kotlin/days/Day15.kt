package days

import utils.readInputLines

object Day15 {
    fun part1(input: String): Int = input.split(',').sumOf { it.lensHash() }

    fun part2(input: String): Int = buildMap<Int, LinkedHashMap<String, Int>> {
        input.split(',')
            .map { it.split('-', '=') }
            .forEach { (label, focalLength) ->
                if (focalLength.isEmpty()) get(label.lensHash())?.let { it -= label }
                else getOrPut(label.lensHash()) { linkedMapOf() }[label] = focalLength.toInt()
            }
    }.entries.sumOf { (boxNumber, box) ->
        box.values.withIndex().sumOf { (entryIndex, focalLength) ->
            (boxNumber + 1) * (entryIndex + 1) * focalLength
        }
    }

    private fun String.lensHash() = fold(0) { acc, char -> ((acc + char.code) * 17) % 256 }
}

fun main() {
    val input = readInputLines("Day15").single()
    println(Day15.part1(input))
    println(Day15.part2(input))
}
