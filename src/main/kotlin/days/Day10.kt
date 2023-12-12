package days

import days.Day10.PipeChar.Pipe
import utils.readInputLines

object Day10 {
    fun part1(input: List<String>): Int = parsePipesDiagram(input).findFarthestPoint()

    fun part2(input: List<String>): Int = TODO()

    private enum class Direction(val xShift: Int = 0, val yShift: Int = 0) {
        NORTH(yShift = -1), EAST(xShift = 1), SOUTH(yShift = 1), WEST(xShift = -1)
    }

    private fun Direction.opposite() = when (this) {
        Direction.NORTH -> Direction.SOUTH
        Direction.EAST -> Direction.WEST
        Direction.SOUTH -> Direction.NORTH
        Direction.WEST -> Direction.EAST
    }

    private sealed interface PipeChar {
        data class Pipe(val first: Direction, val second: Direction) : PipeChar
        data object Start : PipeChar
        data object Ground : PipeChar
    }

    private operator fun Pipe.contains(direction: Direction) = first == direction || second == direction

    private fun parsePipeChar(char: Char): PipeChar = when (char) {
        '|' -> Pipe(Direction.NORTH, Direction.SOUTH)
        '-' -> Pipe(Direction.WEST, Direction.EAST)
        'L' -> Pipe(Direction.NORTH, Direction.EAST)
        'J' -> Pipe(Direction.NORTH, Direction.WEST)
        '7' -> Pipe(Direction.SOUTH, Direction.WEST)
        'F' -> Pipe(Direction.EAST, Direction.SOUTH)
        'S' -> PipeChar.Start
        else -> PipeChar.Ground
    }

    private class PipesDiagram(private val pipeChars: List<List<PipeChar>>, startCoordinates: Coordinates) {
        private val startPipe: PipeOnDiagram

        init {
            val (firstDirection, secondDirection) = Direction.entries.asSequence()
                .filter { direction ->
                    pipeChars.getOrNull(startCoordinates.y + direction.yShift)
                        ?.getOrNull(startCoordinates.x + direction.xShift)
                        .let { it is Pipe && it.contains(direction.opposite()) }
                }.take(2).toList()
            startPipe = PipeOnDiagram(Pipe(firstDirection, secondDirection), startCoordinates.y, startCoordinates.x)
        }

        fun findFarthestPoint() = generatePipeSequence(startPipe, startPipe.pipe.first)
            .zip(generatePipeSequence(startPipe, startPipe.pipe.second))
            .drop(1)
            .takeWhile { (firstPair, secondPair) -> firstPair.first != secondPair.first }
            .count() + 1

        private fun generatePipeSequence(startPipe: PipeOnDiagram, startDirection: Direction) =
            generateSequence(startPipe to startDirection) { (pipe, direction) ->
                val newPipe = nextPipe(pipe, direction)
                newPipe to newPipe.pipe.getNewDirection(direction)
            }

        fun Pipe.getNewDirection(leadingDirection: Direction) =
            if (first.opposite() != leadingDirection) first else second

        fun nextPipe(oldPipe: PipeOnDiagram, direction: Direction): PipeOnDiagram {
            val newY = oldPipe.y + direction.yShift
            val newX = oldPipe.x + direction.xShift
            return PipeOnDiagram(pipeChars[newY][newX] as Pipe, newY, newX)
        }
    }

    private data class Coordinates(val y: Int, val x: Int)
    private data class PipeOnDiagram(val pipe: Pipe, val y: Int, val x: Int)

    private fun parsePipesDiagram(inputLines: List<String>): PipesDiagram {
        lateinit var startCoordinates: Coordinates
        val pipeChars = inputLines.mapIndexed { yIndex, line ->
            line.mapIndexed { xIndex, char ->
                parsePipeChar(char).also { if (it == PipeChar.Start) startCoordinates = Coordinates(yIndex, xIndex) }
            }
        }
        return PipesDiagram(pipeChars, startCoordinates)
    }
}

fun main() {
    val input = readInputLines("Day10")
    val testInput = readInputLines("Day10_test")
    println(Day10.part1(testInput))
    println(Day10.part1(input))
}