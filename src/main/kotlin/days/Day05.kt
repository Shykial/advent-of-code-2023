package days

import utils.rangeIntersect
import utils.readInputLines
import utils.splitBy

object Day05 {
    fun part1(input: List<String>): Long {
        val (seeds, mappings) = parseAlmanac(input)
        return seeds.minOf { initialValue ->
            mappings.fold(initialValue) { currentValue, mappingRanges ->
                mappingRanges.firstNotNullOfOrNull { it.getMappingOrNull(currentValue) } ?: currentValue
            }
        }
    }

    fun part2(input: List<String>): Long {
        val (seedRanges, mappings) = parseAlmanac(input).toPart2()
        return seedRanges.minOf { seedRange ->
            mappings.fold(sequenceOf(seedRange)) { currentRanges, mappingRanges ->
                currentRanges.flatMap { range ->
                    val matchedRanges = mappingRanges.mapNotNull { it.getIntersectedRangeOrNull(range) }
                    mergeMatchedRanges(matchedRanges, range)
                }
            }.minOf { it.first }
        }
    }

    private fun mergeMatchedRanges(matchedRanges: List<MatchedRange>, range: LongRange): Sequence<LongRange> {
        if (matchedRanges.isEmpty()) return sequenceOf(range)
        return sequence {
            val rangeBeforeFirstMatch = range.first..<matchedRanges.first().matchedRange.first
            if (!rangeBeforeFirstMatch.isEmpty()) yield(rangeBeforeFirstMatch)
            matchedRanges.zipWithNext().forEach { (firstMatch, followingMatch) ->
                yield(firstMatch.outcomeRange)
                val rangeBetween = (firstMatch.matchedRange.last + 1..<followingMatch.matchedRange.first)
                if (!rangeBetween.isEmpty()) yield(rangeBetween)
            }
            yield(matchedRanges.last().outcomeRange)
            val rangeAfterLastMatch = (matchedRanges.last().matchedRange.last + 1)..range.last
            if (!rangeAfterLastMatch.isEmpty()) yield(rangeAfterLastMatch)
        }
    }

    private fun parseAlmanac(inputLines: List<String>): ParsedAlmanac {
        val chunks = inputLines.splitBy { it.isBlank() }
        val seeds = chunks[0].single().substringAfter(": ").split(" ").map { it.toLong() }
        val parsedMappings = chunks.drop(1).map(::parseMappingsChunk)
        return ParsedAlmanac(seeds = seeds, mappings = parsedMappings)
    }

    private fun parseMappingsChunk(chunk: List<String>) =
        chunk
            .drop(1)
            .map { line ->
                val (destinationStart, sourceStart, range) = line.split(" ").map { it.toLong() }
                MappingRange(destinationStart, sourceStart, range)
            }

    private data class ParsedAlmanac(val seeds: List<Long>, val mappings: List<List<MappingRange>>)
    private data class ParsedAlmanacPart2(val seeds: List<LongRange>, val sortedMappings: List<List<MappingRange>>)

    private fun ParsedAlmanac.toPart2() = ParsedAlmanacPart2(
        seeds = seeds.chunked(2) { (first, second) -> first..<first + second },
        sortedMappings = mappings.map { it.sortedBy { mappingRange -> mappingRange.sourceRange.first } },
    )

    private class MappingRange(
        destinationStart: Long,
        sourceStart: Long,
        length: Long,
    ) {
        val sourceRange: LongRange = (sourceStart..<sourceStart + length)
        val destinationRange: LongRange = (destinationStart..<destinationStart + length)

        fun getIntersectedRangeOrNull(otherSourceRange: LongRange): MatchedRange? {
            val intersectedRange = (sourceRange rangeIntersect otherSourceRange)
            if (intersectedRange.isEmpty()) return null
            val destinationRangeStart = destinationRange.first + (intersectedRange.first - sourceRange.first)
            val destinationRangeEnd = destinationRange.last - (sourceRange.last - intersectedRange.last)
            return MatchedRange(intersectedRange, destinationRangeStart..destinationRangeEnd)
        }

        fun getMappingOrNull(source: Long) = when (source) {
            in sourceRange -> destinationRange.first + (source - sourceRange.first)
            else -> null
        }
    }

    private data class MatchedRange(val matchedRange: LongRange, val outcomeRange: LongRange)
}

fun main() {
    val input = readInputLines("Day05")
    println(Day05.part1(input))
    println(Day05.part2(input))
}
