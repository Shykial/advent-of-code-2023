package utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.executeAsync
import java.io.File
import java.nio.file.Path
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.name
import kotlin.io.path.useDirectoryEntries

@Suppress("unused")
object InputsDownloader {
    private const val AOC_BASE_PATH = "https://adventofcode.com/2023"
    private val daysInputFilesRegex = Regex("""Day(\d\d)\.txt""")
    private val lastDayOfAoc = OffsetDateTime.of(2023, 12, 25, 0, 0, 0, 0, ZoneOffset.UTC)
    private val httpClient = OkHttpClient()

    suspend fun downloadMissingInputs(inputDirectory: Path, sessionCookie: String) = withContext(Dispatchers.IO) {
        val daysWithMissingInputs = getDaysWithMissingInputs(inputDirectory)
        println("Downloading inputs for days $daysWithMissingInputs")
        val counter = AtomicInteger()
        daysWithMissingInputs.forEach { dayNumber ->
            launch {
                val downloadedInput = httpClient
                    .newCall(buildAocDayRequest(dayNumber, sessionCookie))
                    .executeAsync()
                    .body.string()
                createAocInputFile(inputDirectory, dayNumber, downloadedInput)
                println("Downloaded input for day $dayNumber\t-\t${counter.incrementAndGet()}/${daysWithMissingInputs.size}")
            }
        }
    }

    private fun getDaysWithMissingInputs(inputDirectory: Path): List<Int> {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val availableDays = 1..minOf(now, lastDayOfAoc).dayOfMonth
        val existingInputDays = inputDirectory.useDirectoryEntries { entries ->
            entries
                .mapNotNull { daysInputFilesRegex.find(it.name)?.groupValues?.get(1)?.toInt() }
                .toSet()
        }
        return availableDays - existingInputDays
    }

    private fun createAocInputFile(inputDirectory: Path, dayNumber: Int, downloadedInput: String) {
        File(inputDirectory.toFile(), "Day${dayNumber.toString().padStart(2, '0')}.txt")
            .apply { writeText(downloadedInput) }
            .createNewFile()
    }

    private fun buildAocDayRequest(dayNumber: Int, sessionCookie: String) =
        Request.Builder()
            .get()
            .url("$AOC_BASE_PATH/day/$dayNumber/input")
            .header("cookie", "session=$sessionCookie")
            .build()
}
