package utils

data class Coordinates(val y: Int, val x: Int)
data class LongCoordinates(val y: Long, val x: Long)

enum class Direction(
    val xShift: Int = 0,
    val yShift: Int = 0
) {
    NORTH(yShift = -1), EAST(xShift = 1), SOUTH(yShift = 1), WEST(xShift = -1);
}

fun Direction.clockwiseOffset(offset: Int) =
    if (offset == 0) this else Direction.entries[(ordinal + offset) % Direction.entries.size]

fun Direction.opposite() = clockwiseOffset(2)

operator fun Coordinates.plus(direction: Direction) =
    Coordinates(y = y + direction.yShift, x = x + direction.xShift)

operator fun List<String>.get(coordinates: Coordinates) = this[coordinates.y][coordinates.x]
fun List<String>.getOrNull(coordinates: Coordinates) = getOrNull(coordinates.y)?.getOrNull(coordinates.x)

operator fun <T> List<List<T>>.get(coordinates: Coordinates) = this[coordinates.y][coordinates.x]
fun <T> List<List<T>>.getOrNull(coordinates: Coordinates) = getOrNull(coordinates.y)?.getOrNull(coordinates.x)