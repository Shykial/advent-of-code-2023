import kotlin.math.nextDown
import kotlin.math.sqrt

object Day06 {
    fun part1(input: List<String>): Int =
        parseRaces(input).fold(1) { acc, race -> acc * race.countPossibilitiesToWin() }

    fun part2(input: List<String>): Int = parseRacePart2(input).countPossibilitiesToWin()

    private fun parseRaces(inputLines: List<String>): Sequence<Race> {
        val (times, distances) = inputLines
            .map { line -> digitsRegex.findAll(line).map { it.value.toLong() } }
        return times.zip(distances) { time, distance -> Race(time, distance) }
    }

    private fun parseRacePart2(inputLines: List<String>): Race {
        val (time, distance) = inputLines.map { it.filter(Char::isDigit).toLong() }
        return Race(time, distance)
    }

    private data class Race(val timeMs: Long, val recordMm: Long)

    private fun Race.countPossibilitiesToWin(): Int {
        val deltaRoot = sqrt(timeMs * timeMs - 4.0 * recordMm)
        val minValue = ((timeMs - deltaRoot) / 2 + 1).toInt()
        val maxValue = ((timeMs + deltaRoot) / 2).nextDown().toInt()
        return maxValue - minValue + 1
    }
}

fun main() {
    val input = readInputLines("Day06")
    println(Day06.part1(input))
    println(Day06.part2(input))
}
