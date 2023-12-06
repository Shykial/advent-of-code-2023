import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInputLines(name: String) = Path("src/$name.txt").readLines()

fun readInput(name: String) = Path("src/$name.txt").readText()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun IntRange.widenedByOne() = (first - 1..last + 1)

fun Int.touches(range: IntRange) = this == range.first - 1 || this == range.last + 1

infix fun LongRange.rangeIntersect(other: LongRange) = maxOf(first, other.first)..minOf(last, other.last)
