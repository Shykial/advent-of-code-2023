object Day04 {
    private val numbersRegex = Regex("""\d+""")

    fun part1(input: List<String>): Int =
        input.asSequence()
            .map { parseCardString(it) }
            .mapNotNull { card -> (card.chosenNumbers intersect card.winningNumbers).size.takeIf { it > 0 } }
            .sumOf { 1 shl (it - 1) }

    fun part2(input: List<String>): Int {
        val matchingNumbersCounts = input
            .map { parseCardString(it) }
            .map { (it.chosenNumbers intersect it.winningNumbers).size }
        val multipliers = mutableMapOf<Int, Int>()
        var cardsCount = 0

        matchingNumbersCounts.forEachIndexed { index, matchingCount ->
            val multiplier = multipliers.getOrDefault(index, 1)
            cardsCount += multiplier
            (index + 1..index + matchingCount).forEach {
                multipliers.merge(it, 1 + multiplier) { old, _ -> old + multiplier }
            }
        }
        return cardsCount
    }

    private fun parseCardString(cardString: String): Scratchcard {
        val (winningNumbers, chosenNumbers) = cardString
            .substringAfter(": ")
            .split(" | ")
            .map { numbersRegex.findAll(it).map { match -> match.value.toInt() }.toSet() }
        return Scratchcard(winningNumbers, chosenNumbers)
    }

    private data class Scratchcard(val winningNumbers: Set<Int>, val chosenNumbers: Set<Int>)
}

fun main() {
    val input = readInputLines("Day04")
    println(Day04.part1(input))
    println(Day04.part2(input))
}
