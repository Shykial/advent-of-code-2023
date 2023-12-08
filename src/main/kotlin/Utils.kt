val digitsRegex = Regex("""\d+""")

fun readInputLines(name: String) = readResourceStream("inputs/$name.txt").bufferedReader().readLines()

fun readInput(name: String) = readResourceStream("inputs/$name.txt").bufferedReader().readText()

fun IntRange.widenedByOne() = (first - 1..last + 1)

fun Int.touches(range: IntRange) = this == range.first - 1 || this == range.last + 1

infix fun LongRange.rangeIntersect(other: LongRange) = maxOf(first, other.first)..minOf(last, other.last)

private fun readResourceStream(path: String) =
    requireNotNull(object {}::class.java.classLoader.getResourceAsStream(path)) { "Resource $path not found" }