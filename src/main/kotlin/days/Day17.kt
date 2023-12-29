package days

import utils.Coordinates
import utils.Direction
import utils.clockwiseOffset
import utils.getOrNull
import utils.plus
import utils.readInputLines
import java.util.PriorityQueue

object Day17 {
    fun part1(input: List<String>): Int = parseGrid(input).countMinimalHeatLossAStar()

    fun part2(input: List<String>): Int = parseGrid(input).countMinimalHeatLossAStarPart2()

    private fun parseGrid(input: List<String>) =
        input
            .map { line -> line.map { it.digitToInt() } }
            .run(::BlocksGrid)

    private class BlocksGrid(private val blocks: List<List<Int>>) {
        private val endCoordinates = Coordinates(blocks.lastIndex, blocks.first().lastIndex)

        private val startingNodes = listOf(Direction.EAST, Direction.SOUTH).map {
            BlockNode(move = NodeMove(Coordinates(y = 0, x = 0), it), sameDirectionStreak = 0, totalHeatLoss = 0)
        }

        private val heuristicComparator = compareBy<BlockNode> { it.heuristicCost() }

        fun countMinimalHeatLossAStar(): Int {
            val openNodes = PriorityQueue(heuristicComparator).apply { add(startingNodes.first()) }
            val visitedStreaksPerMove = hashMapOf<NodeMove, Int>()

            while (true) {
                openNodes.remove()
                    .getAvailableNodesPart1()
                    .forEach { node ->
                        if (node.move.coordinates == endCoordinates) return node.totalHeatLoss
                        val seenStreak = visitedStreaksPerMove[node.move]
                        if (seenStreak == null || seenStreak > node.sameDirectionStreak) {
                            visitedStreaksPerMove[node.move] = node.sameDirectionStreak
                            openNodes += node
                        }
                    }
            }
        }

        fun countMinimalHeatLossAStarPart2(): Int {
            val openNodes = PriorityQueue(heuristicComparator).apply { addAll(startingNodes) }
            val visitedStreaksNoWobbling = hashMapOf<NodeMove, Int>()
            val visitedStreaksWobbling = hashMapOf<NodeMove, Int>()
            fun mapForStreak(streak: Int) = if (streak < 4) visitedStreaksNoWobbling else visitedStreaksWobbling

            while (true) {
                openNodes.remove()
                    .getAvailableNodesPart2()
                    .forEach { node ->
                        if (node.move.coordinates == endCoordinates) {
                            if (node.sameDirectionStreak >= 4) return node.totalHeatLoss
                            return@forEach
                        }
                        val map = mapForStreak(node.sameDirectionStreak)
                        val shouldAddNode = when (map) {
                            visitedStreaksNoWobbling -> map[node.move].let { it == null || it < node.sameDirectionStreak }
                            else -> map[node.move].let { it == null || it > node.sameDirectionStreak }
                        }
                        if (shouldAddNode) {
                            map[node.move] = node.sameDirectionStreak
                            openNodes += node
                        }
                    }
            }
        }


        private fun Coordinates.distanceFromEnd() = endCoordinates.y - y + endCoordinates.x - x

        private data class BlockNode(val move: NodeMove, val sameDirectionStreak: Int, val totalHeatLoss: Int)

        private data class NodeMove(val coordinates: Coordinates, val direction: Direction)

        private fun BlockNode.heuristicCost() = totalHeatLoss + move.coordinates.distanceFromEnd()

        private fun BlockNode.getAvailableNodesPart1(): List<BlockNode> {
            val offsets = when {
                sameDirectionStreak < 3 -> listOf(1 to 0, 3 to 0, 0 to sameDirectionStreak)
                else -> listOf(1 to 0, 3 to 0)
            }
            return getAvailableNodes(offsets)
        }

        private fun BlockNode.getAvailableNodesPart2(): List<BlockNode> {
            val offsets = when {
                sameDirectionStreak < 4 -> listOf(0 to sameDirectionStreak)
                sameDirectionStreak < 10 -> listOf(1 to 0, 3 to 0, 0 to sameDirectionStreak)
                else -> listOf(1 to 0, 3 to 0)
            }
            return getAvailableNodes(offsets)
        }

        private fun BlockNode.getAvailableNodes(offsetsMappings: List<Pair<Int, Int>>) =
            offsetsMappings.mapNotNull { (offset, oldCount) ->
                val newDirection = move.direction.clockwiseOffset(offset)
                val newCoordinates = move.coordinates + newDirection
                blocks.getOrNull(newCoordinates)?.let { blockValue ->
                    BlockNode(
                        move = NodeMove(newCoordinates, newDirection),
                        sameDirectionStreak = oldCount + 1,
                        totalHeatLoss = totalHeatLoss + blockValue,
                    )
                }
            }
    }
}

fun main() {
    val input = readInputLines("Day17")
    println(Day17.part1(input))
    println(Day17.part2(input))
}