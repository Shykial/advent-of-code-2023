package days

import org.apache.commons.math3.util.ArithmeticUtils
import utils.readInputLines

object Day08 {
    private val mapLettersRegex = Regex("""\w+""")
    private const val START_ELEMENT = "AAA"
    private const val END_ELEMENT = "ZZZ"

    fun part1(input: List<String>): Long {
        val (instructions, mapPairs) = parseDesertMap(input)
        return generateSequence { instructions }.flatten()
            .runningFold(START_ELEMENT) { current, direction ->
                val newPair = mapPairs[current]!!
                if (direction == 'R') newPair.right else newPair.left
            }.indexOf(END_ELEMENT).toLong()
    }

    fun part2(input: List<String>): Long {
        val (instructions, mapPairs) = parseDesertMap(input)
        val startingNodes = mapPairs.keys.filter { it.endsWith('A') }
        val endValuesForNodes = mapPairs.mapValues { (initialValue, _) ->
            instructions.fold(initialValue) { value, direction ->
                val newPair = mapPairs[value]!!
                if (direction == 'R') newPair.right else newPair.left
            }
        }
        val nodesWinningRound = endValuesForNodes.asSequence()
            .filter { (_, endValue) -> endValue.endsWith('Z') }
            .map { it.key }
            .toSet()

        val winningIterations = startingNodes.asSequence().map { startingNode ->
            generateSequence(startingNode) { value ->
                endValuesForNodes[value]!!
            }.indexOfFirst { it in nodesWinningRound } + 1L
        }
        return winningIterations.reduce { acc, n -> ArithmeticUtils.lcm(acc, n) } * instructions.size
    }

    private fun parseDesertMap(desertMapLines: List<String>): DesertMap {
        val instructions = desertMapLines[0].toList()
        val mapPairs = desertMapLines.subList(2, desertMapLines.size).associate { line ->
            val (element, left, right) = mapLettersRegex.findAll(line).map { it.value }.toList()
            element to MapPair(left, right)
        }
        return DesertMap(instructions, mapPairs)
    }

    private data class MapPair(val left: String, val right: String)
    private data class DesertMap(val instructions: List<Char>, val mapPairs: Map<String, MapPair>)
}

fun main() {
    val input = readInputLines("Day08")
    println(Day08.part1(input))
    println(Day08.part2(input))
}