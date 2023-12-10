package days

import utils.atLeastNMatch
import utils.readInputLines

private typealias CountedHand = Map<Char, Int>

object Day07 {
    private val cardStrengths = (listOf('A', 'K', 'Q', 'J', 'T') + ('9' downTo '2'))
        .withIndex()
        .associate { (index, card) -> card to index }

    private val cardStrengthsWithJokers = cardStrengths + ('J' to cardStrengths.size)

    fun part1(input: List<String>): Int = solve(input, jokerMode = false)

    fun part2(input: List<String>): Int = solve(input, jokerMode = true)

    private fun solve(input: List<String>, jokerMode: Boolean): Int {
        val predicates = if (jokerMode) sortedHandPredicatesWithJokers else sortedHandPredicates
        val cardStrengths = if (jokerMode) cardStrengthsWithJokers else cardStrengthsWithJokers
        return input.asSequence().map { line ->
            val hand = parseCamelCardsHand(line)
            val countedHand = hand.toCountedHand()
            hand to predicates.indexOfFirst { it(countedHand) }
        }.sortedWith(
            compareByDescending<Pair<CamelCardsHand, Int>> { (_, value) -> value }
                .thenBy(cardsStrengthComparator(cardStrengths)) { it.first.cards }
        ).foldIndexed(0) { index, acc, (hand, _) -> acc + hand.bid * (index + 1) }
    }

    private fun parseCamelCardsHand(camelCardsHandString: String): CamelCardsHand {
        val (cardsString, bidString) = camelCardsHandString.split(" ")
        return CamelCardsHand(cardsString, bidString.toInt())
    }

    private data class CamelCardsHand(val cards: String, val bid: Int)

    private fun CamelCardsHand.toCountedHand() = cards.groupingBy { it }.eachCount()

    private val sortedHandPredicates = listOf<(CountedHand) -> Boolean>(
        { it.hasNOfKind(5) },
        { it.hasNOfKind(4) },
        { it.hasFullHouse() },
        { it.hasNOfKind(3) },
        { it.hasNPairs(2) },
        { it.hasNPairs(1) },
        { true }
    )

    private val sortedHandPredicatesWithJokers = listOf<(CountedHand) -> Boolean>(
        { it.hasNOfKindWithJokers(5) },
        { it.hasNOfKindWithJokers(4) },
        { it.hasFullHouseWithJokers() },
        { it.hasNOfKindWithJokers(3) },
        { it.hasNPairs(2) }, // two pairs not possible with jokers
        { it.hasNOfKindWithJokers(2) },
        { true }
    )

    private fun cardsStrengthComparator(source: Map<Char, Int>) = Comparator<String> { firstCards, secondCards ->
        (firstCards.asSequence() zip secondCards.asSequence())
            .first { it.first != it.second }
            .let { source[it.second]!! - source[it.first]!! }
    }

    private fun CountedHand.hasNOfKind(n: Int) = values.any { it == n }
    private fun CountedHand.hasFullHouse() = values.any { it == 3 } && values.any { it == 2 }
    private fun CountedHand.hasNPairs(n: Int) = values.atLeastNMatch(n) { it == 2 }

    private fun CountedHand.hasNOfKindWithJokers(n: Int): Boolean =
        keys == setOf('J') || entries.any { (card, count) -> card != 'J' && count + jokersCount == n }

    private fun CountedHand.hasFullHouseWithJokers(): Boolean {
        val cardWithThree = entries.firstOrNull { (card, count) -> card != 'J' && count + jokersCount == 3 }?.key
        return cardWithThree != null &&
            entries.any { (card, count) -> card != cardWithThree && card != 'J' && count == 2 }
    }

    private val CountedHand.jokersCount get() = getOrDefault('J', 0)
}

fun main() {
    val input = readInputLines("Day07")
    println(Day07.part1(input))
    println(Day07.part2(input))
}