object Day03 {
    private val numbersRegex = Regex("""\d+""")
    private val symbolsRegex = Regex("""[^\d.]""")
    private const val GEAR_SYMBOL = '*'

    fun part1(input: List<String>): Int {
        val (numbers, symbolsCoordinates) = input.parseEngineNumbersAndCoordinates { line ->
            symbolsRegex.findAll(line).map { it.range.single() }.toList()
        }

        return numbers.asSequence().mapIndexed { y, numbersInLine ->
            numbersInLine.asSequence().filter { number ->
                (y != 0 && symbolsCoordinates[y - 1].any { it in number.xRange.widenedByOne() }) ||
                    symbolsCoordinates[y].any { it.touches(number.xRange) } ||
                    (y != numbers.lastIndex && symbolsCoordinates[y + 1].any { it in number.xRange.widenedByOne() })
            }.sumOf { it.value }
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val (numbers, gearsCoordinates) = input.parseEngineNumbersAndCoordinates { line ->
            line.indices.filter { line[it] == GEAR_SYMBOL }
        }
        return gearsCoordinates.asSequence().flatMapIndexed { y, gearLineCoordinates ->
            gearLineCoordinates.mapNotNull { x ->
                buildList {
                    if (y != 0) numbers[y - 1].filter { x in it.xRange.widenedByOne() }.let(::addAll)
                    numbers[y].filter { x.touches(it.xRange) }.let(::addAll)
                    if (y != numbers.lastIndex) numbers[y + 1].filter { x in it.xRange.widenedByOne() }.let(::addAll)
                }.takeIf { it.size == 2 }
            }
        }.fold(0) { acc, (first, second) -> acc + first.value * second.value }
    }

    private inline fun <T> List<String>.parseEngineNumbersAndCoordinates(
        coordinatesSelector: (line: String) -> List<T>
    ): Pair<List<List<EngineNumber>>, List<List<T>>> = mapIndexed { index, line ->
        numbersRegex.findAll(line).map {
            EngineNumber(y = index, xRange = it.range, value = it.value.toInt())
        }.toList() to coordinatesSelector(line)
    }.unzip()

    private data class EngineNumber(val y: Int, val xRange: IntRange, val value: Int)
}

fun main() {
    val input = readInputLines("Day03")
    println(Day03.part1(input))
    println(Day03.part2(input))
}
