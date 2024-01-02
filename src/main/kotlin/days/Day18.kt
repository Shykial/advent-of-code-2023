package days

import days.Day18.GridSymbol.Trench
import utils.Coordinates
import utils.Direction
import utils.clockwiseOffset
import utils.getOrNull
import utils.mostFrequent
import utils.plus
import utils.readInputLines
import utils.set
import utils.takeWhileNotNull

object Day18 {
    fun part1(input: List<String>): Int {
        val grid = input.map { it.parseMove() }.run(::parseDiggingGrid)

        val mainLoopPipeCoordinates = grid.trenchMoves.map { it.trench.coordinates }.toSet()
        val count = grid.trenchMoves.asSequence()
            .flatMap { (trench, direction) ->
                val scanningDirection = direction.clockwiseOffset(if (grid.turn == TurnDirection.CLOCKWISE) 1 else 3)
                generateSequence(trench.coordinates) { it + scanningDirection }
                    .drop(1)
                    .map { it.takeIf { grid.gridSymbols.getOrNull(it) != null && it !in mainLoopPipeCoordinates } }
                    .takeWhileNotNull()
            }.distinct().count() + mainLoopPipeCoordinates.size
        return count
    }

    fun part2(input: List<String>): Int = TODO()

    private fun String.parseMove() = split(' ').let { (direction, meters, hex) ->
        Move(
            direction = directionFromRelative(direction.single()),
            meters = meters.toInt(),
            hexColor = hex.substringAfter('#').substringBefore(')')
        )
    }

    private fun directionFromRelative(relativeDirection: Char) = when (relativeDirection) {
        'U' -> Direction.NORTH
        'D' -> Direction.SOUTH
        'R' -> Direction.EAST
        'L' -> Direction.WEST
        else -> error("Unsupported direction")
    }

    private data class Move(
        val direction: Direction,
        val meters: Int,
        val hexColor: String
    )

    private fun parseDiggingGrid(moves: List<Move>): DiggingGrid {
        val firstTrench = TrenchOnDiagram(Trench(moves.first().hexColor), Coordinates(0, 0))
        val flattenedMoves = moves.flatMap { move -> List(move.meters) { move.direction to move.hexColor } }
        val direction = flattenedMoves.asSequence()
            .map { it.first }
            .zipWithNext()
            .filter { it.first != it.second }
            .map { getTurnDirection(it.first, it.second) }
            .mostFrequent()

        val trenchMoves = flattenedMoves.runningFold(
            TrenchMove(firstTrench, flattenedMoves.first().first)
        ) { move, (direction, hexColor) ->
            TrenchMove(TrenchOnDiagram(Trench(hexColor), move.trench.coordinates + direction), direction)
        }

        val yLength = trenchMoves.maxOf { it.trench.coordinates.y } + 1
        val xLength = trenchMoves.maxOf { it.trench.coordinates.x } + 1
        val grid = List(yLength) { MutableList<GridSymbol>(xLength) { GridSymbol.Dot } }.apply {
            trenchMoves.forEach { this[it.trench.coordinates] = it.trench.trench }
        }
        return DiggingGrid(gridSymbols = grid, turn = direction, trenchMoves = trenchMoves)
    }

    private data class DiggingGrid(
        val gridSymbols: List<List<GridSymbol>>,
        val turn: TurnDirection,
        val trenchMoves: List<TrenchMove>
    )

    private sealed interface GridSymbol {
        data object Dot : GridSymbol
        data class Trench(val hexColor: String) : GridSymbol
    }

    private fun getTurnDirection(firstDirection: Direction, secondDirection: Direction) =
        when (secondDirection) {
            firstDirection.clockwiseOffset(1) -> TurnDirection.CLOCKWISE
            else -> TurnDirection.ANTICLOCKWISE
        }

    private enum class TurnDirection { CLOCKWISE, ANTICLOCKWISE }
    private data class TrenchOnDiagram(val trench: Trench, val coordinates: Coordinates)
    private data class TrenchMove(val trench: TrenchOnDiagram, val direction: Direction)
}

fun main() {
    val input = readInputLines("Day18")
//    println(Day18.part1(input))
//    println(Day18.part2(input))
}