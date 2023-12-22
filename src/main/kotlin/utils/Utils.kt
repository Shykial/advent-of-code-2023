package utils

val digitsRegex = Regex("""\d+""")

fun readInputLines(name: String) = readResourceStream("inputs/$name.txt").bufferedReader().readLines()

fun IntRange.widenedByOne() = (first - 1..last + 1)

fun Int.touches(range: IntRange) = this == range.first - 1 || this == range.last + 1

infix fun LongRange.rangeIntersect(other: LongRange) = maxOf(first, other.first)..minOf(last, other.last)

inline fun <T> Iterable<T>.atLeastNMatch(n: Int, predicate: (T) -> Boolean): Boolean {
    var matched = 0
    forEach { element ->
        if (predicate(element)) matched++
        if (matched == n) return true
    }
    return false
}

private fun readResourceStream(path: String) =
    requireNotNull(object {}::class.java.classLoader.getResourceAsStream(path)) { "Resource $path not found" }

@Suppress("UNCHECKED_CAST")
fun <T : Any> Sequence<T?>.takeWhileNotNull() = takeWhile { it != null } as Sequence<T>

inline fun <T> Iterable<T>.splitBy(delimiterPredicate: (T) -> Boolean): List<List<T>> = buildList {
    var currentAggregate: MutableList<T>? = null
    for (element in this@splitBy) {
        if (delimiterPredicate(element)) {
            currentAggregate?.let { this += it }
            currentAggregate = null
        } else {
            if (currentAggregate == null) currentAggregate = mutableListOf(element)
            else currentAggregate += element
        }
    }
    currentAggregate?.let { this += it }
}

inline fun <T> Sequence<T>.splitBy(crossinline delimiterPredicate: (T) -> Boolean): Sequence<List<T>> = sequence {
    var currentAggregate: MutableList<T>? = null
    for (element in this@splitBy) {
        if (delimiterPredicate(element)) {
            currentAggregate?.let { yield(it) }
            currentAggregate = null
        } else {
            if (currentAggregate == null) currentAggregate = mutableListOf(element)
            else currentAggregate += element
        }
    }
    currentAggregate?.let { yield(it) }
}

fun String.cutAt(index: Int): Pair<String, String> = take(index) to drop(index)

fun String.replaceCharAt(index: Int, newChar: Char) = StringBuilder(this).apply { this[index] = newChar }.toString()

inline fun <T : Any> T?.onNull(block: () -> Unit) = also { if (it == null) block() }
