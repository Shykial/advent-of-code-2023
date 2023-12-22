package days

import days.Day10.DiagramSymbol.Pipe
import utils.Coordinates
import utils.Direction
import utils.getOrNull
import utils.plus
import utils.readInputLines
import utils.takeWhileNotNull

object Day10 {
    fun part1(input: List<String>): Int = parsePipesDiagram(input).findFarthestPoint()

    fun part2(input: List<String>): Int = parsePipesDiagram(input).countEnclosedTiles()

    private fun Direction.clockwiseOffset(offset: Int) = Direction.entries[(ordinal + offset) % Direction.entries.size]
    private fun Direction.opposite() = clockwiseOffset(2)

    private sealed interface DiagramSymbol {
        data class Pipe(val firstDirection: Direction, val secondDirection: Direction) : DiagramSymbol
        data object Start : DiagramSymbol
        data object Ground : DiagramSymbol
    }

    private operator fun Pipe.contains(direction: Direction) =
        firstDirection == direction || secondDirection == direction

    private fun parsePipeChar(char: Char): DiagramSymbol = when (char) {
        '|' -> Pipe(Direction.NORTH, Direction.SOUTH)
        '-' -> Pipe(Direction.WEST, Direction.EAST)
        'L' -> Pipe(Direction.NORTH, Direction.EAST)
        'J' -> Pipe(Direction.NORTH, Direction.WEST)
        '7' -> Pipe(Direction.SOUTH, Direction.WEST)
        'F' -> Pipe(Direction.EAST, Direction.SOUTH)
        'S' -> DiagramSymbol.Start
        else -> DiagramSymbol.Ground
    }

    private class PipesDiagram(private val symbols: List<List<DiagramSymbol>>, startCoordinates: Coordinates) {
        private val startingPipe = PipeOnDiagram(findStartingPipe(startCoordinates), startCoordinates)

        fun findFarthestPoint() = getPipeMovesSequence(startingPipe, startingPipe.pipe.firstDirection)
            .zip(getPipeMovesSequence(startingPipe, startingPipe.pipe.secondDirection))
            .drop(1)
            .takeWhile { (first, second) -> first.pipe != second.pipe }
            .count() + 1

        fun countEnclosedTiles(): Int {
            val mainLoopPipeMoves = getMainLoopPipeMoves()
            val loopTurn = getLoopDirection(mainLoopPipeMoves)
            return getEnclosedTilesScanningSequence(mainLoopPipeMoves, loopTurn).count()
        }

        private fun getEnclosedTilesScanningSequence(
            mainLoopPipeMoves: List<PipeMove>,
            loopTurn: TurnDirection,
        ): Sequence<Coordinates> {
            val mainLoopPipeCoordinates = mainLoopPipeMoves.map { it.pipe.coordinates }.toSet()
            return mainLoopPipeMoves.asSequence()
                .flatMap { (pipe, direction) ->
                    setOf(direction, pipe.pipe.getOtherDirection(direction).opposite()).asSequence()
                        .map { it.clockwiseOffset(if (loopTurn == TurnDirection.CLOCKWISE) 1 else 3) }
                        .flatMap { scanningDirection ->
                            generateSequence(pipe.coordinates) { it + scanningDirection }
                                .drop(1)
                                .map { it.takeIf { symbols.getOrNull(it) != null && it !in mainLoopPipeCoordinates } }
                                .takeWhileNotNull()
                        }
                }.distinct()
        }

        private fun findStartingPipe(startCoordinates: Coordinates) = Direction.entries.asSequence()
            .filter { direction ->
                val symbol = symbols.getOrNull(startCoordinates + direction)
                symbol is Pipe && direction.opposite() in symbol
            }.take(2).toList()
            .let { (firstDirection, secondDirection) -> Pipe(firstDirection, secondDirection) }

        fun getMainLoopPipeMoves() = buildList {
            add(PipeMove(startingPipe, startingPipe.pipe.firstDirection))
            getPipeMovesSequence(startingPipe, startingPipe.pipe.firstDirection)
                .drop(1)
                .takeWhile { (pipe, _) -> pipe != startingPipe }
                .run(::addAll)
        }

        private fun getLoopDirection(loopMoves: List<PipeMove>) =
            loopMoves.asSequence()
                .filter { (pipe, _) -> pipe.pipe.firstDirection != pipe.pipe.secondDirection.opposite() }
                .map { (pipe, direction) -> getTurnDirection(direction, pipe.pipe.getOtherDirection(direction)) }
                .groupingBy { it }
                .eachCount()
                .maxBy { it.value }
                .key

        private fun getTurnDirection(firstDirection: Direction, secondDirection: Direction) =
            when (secondDirection) {
                firstDirection.clockwiseOffset(1) -> TurnDirection.CLOCKWISE
                else -> TurnDirection.ANTICLOCKWISE
            }

        private fun getPipeMovesSequence(startPipe: PipeOnDiagram, startDirection: Direction) =
            generateSequence(PipeMove(startPipe, startDirection)) { (pipe, direction) ->
                val newPipe = nextPipe(pipe, direction)
                PipeMove(newPipe, newPipe.pipe.getOtherDirection(direction.opposite()))
            }

        private fun Pipe.getOtherDirection(direction: Direction) =
            firstDirection.takeIf { it != direction } ?: secondDirection

        fun nextPipe(oldPipe: PipeOnDiagram, direction: Direction): PipeOnDiagram {
            val newCoordinates = oldPipe.coordinates + direction
            val newPipe = symbols.getOrNull(newCoordinates)
                .also { if (it is DiagramSymbol.Start) return startingPipe }
            return PipeOnDiagram(newPipe as Pipe, newCoordinates)
        }
    }

    private data class PipeOnDiagram(val pipe: Pipe, val coordinates: Coordinates)
    private data class PipeMove(val pipe: PipeOnDiagram, val direction: Direction)
    private enum class TurnDirection { CLOCKWISE, ANTICLOCKWISE }

    private fun parsePipesDiagram(inputLines: List<String>): PipesDiagram {
        lateinit var startCoordinates: Coordinates
        val diagramSymbols = inputLines.mapIndexed { yIndex, line ->
            line.mapIndexed { xIndex, char ->
                parsePipeChar(char)
                    .also { if (it == DiagramSymbol.Start) startCoordinates = Coordinates(yIndex, xIndex) }
            }
        }
        return PipesDiagram(diagramSymbols, startCoordinates)
    }
}

fun main() {
    val input = readInputLines("Day10")
    println(Day10.part1(input))
    println(Day10.part2(input))
}
