object Day02 {
    fun part1(input: List<String>): Int {
        val initialLoad = mapOf(CubeColor.RED to 12, CubeColor.GREEN to 13, CubeColor.BLUE to 14)
        return input.asSequence()
            .map { parseGame(it) }
            .filter {
                it.shownSets.all { shownSet ->
                    initialLoad.all { (color, initialLoad) ->
                        shownSet.getOrDefault(color, 0) <= initialLoad
                    }
                }
            }.sumOf { it.id }
    }

    fun part2(input: List<String>): Int =
        input.asSequence()
            .map { parseGame(it) }
            .sumOf { game ->
                CubeColor.entries.fold(1) { acc: Int, color ->
                    acc * game.shownSets.maxOf { shownSet -> shownSet[color] ?: 0 }
                }
            }

    private fun parseGame(gameString: String): CubeGame {
        val (gameIdString, shownSetsString) = gameString.split(": ")
        val gameId = gameIdString.substringAfter(' ').toInt()
        val shownSets = shownSetsString
            .split("; ")
            .map { parseCubeSet(it) }
        return CubeGame(gameId, shownSets)
    }

    private fun parseCubeSet(cubeSetString: String): Map<CubeColor, Int> =
        cubeSetString
            .split(", ")
            .associate {
                val (countString, colorString) = it.split(' ')
                CubeColor.valueOf(colorString.uppercase()) to countString.toInt()
            }

    private enum class CubeColor { RED, GREEN, BLUE }

    private data class CubeGame(
        val id: Int,
        val shownSets: List<Map<CubeColor, Int>>,
    )
}

fun main() {
    val input = readInputLines("Day02")
    println(Day02.part1(input))
    println(Day02.part2(input))
}
