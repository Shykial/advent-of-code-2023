package days

import utils.Coordinates
import utils.Direction
import utils.getOrNull
import utils.plus
import utils.readInputLines

object Day16 {
    fun part1(input: List<String>): Int {
        val startCoordinates = Coordinates(0, 0)
        val start = Move(startCoordinates, input[0][0], Direction.EAST)
        return BeamsGrid(input).countEnergizedTiles(start)
    }

    fun part2(input: List<String>): Int {
        val grid = BeamsGrid(input)
        val lastXIndex = input.first().lastIndex
        val starts = sequence {
            input.first().forEachIndexed { x, char ->
                yield(Move(Coordinates(0, x), char, Direction.SOUTH))
            }
            input.last().forEachIndexed { x, char ->
                yield(Move(Coordinates(input.lastIndex, x), char, Direction.NORTH))
            }
            input.indices.forEach { y ->
                yield(Move(Coordinates(y, 0), input[y][0], Direction.EAST))
                yield(Move(Coordinates(y, lastXIndex), input[y][lastXIndex], Direction.WEST))
            }
        }
        return starts.maxOf { grid.countEnergizedTiles(it) }
    }

    private class BeamsGrid(private val rows: List<String>) {
        fun countEnergizedTiles(start: Move): Int {
            val visitedMoves = mutableSetOf(start)
            var currentMoves = listOf(start)
            while (currentMoves.isNotEmpty()) {
                currentMoves = currentMoves.flatMap { move ->
                    move.nextMovesSequence().filter { visitedMoves.add(it) }
                }
            }
            return visitedMoves.distinctBy { it.coordinates }.size
        }

        private fun Move.nextMovesSequence(): Sequence<Move> =
            when (char) {
                '.' -> moveTowards(direction)?.let { sequenceOf(it) }.orEmpty()
                '-' -> when (direction) {
                    Direction.WEST, Direction.EAST -> moveTowards(direction)?.let { sequenceOf(it) }.orEmpty()

                    Direction.NORTH, Direction.SOUTH -> sequenceOf(Direction.EAST, Direction.WEST)
                        .mapNotNull { moveTowards(it) }
                }

                '|' -> when (direction) {
                    Direction.NORTH, Direction.SOUTH -> moveTowards(direction)?.let { sequenceOf(it) }.orEmpty()
                    Direction.EAST, Direction.WEST -> sequenceOf(Direction.NORTH, Direction.SOUTH)
                        .mapNotNull { moveTowards(it) }
                }

                '/' -> {
                    val newDirection = when (direction) {
                        Direction.NORTH -> Direction.EAST
                        Direction.EAST -> Direction.NORTH
                        Direction.SOUTH -> Direction.WEST
                        Direction.WEST -> Direction.SOUTH
                    }
                    moveTowards(newDirection)?.let { sequenceOf(it) }.orEmpty()
                }

                else -> {
                    val newDirection = when (direction) {
                        Direction.NORTH -> Direction.WEST
                        Direction.EAST -> Direction.SOUTH
                        Direction.SOUTH -> Direction.EAST
                        Direction.WEST -> Direction.NORTH
                    }
                    moveTowards(newDirection)?.let { sequenceOf(it) }.orEmpty()
                }
            }


        private fun Move.moveTowards(direction: Direction): Move? {
            val newCoordinates = coordinates + direction
            return rows.getOrNull(newCoordinates)?.let { Move(newCoordinates, it, direction) }
        }
    }

    private data class Move(val coordinates: Coordinates, val char: Char, val direction: Direction)
}

fun main() {
    val input = readInputLines("Day16")
    println(Day16.part1(input))
    println(Day16.part2(input))
}
