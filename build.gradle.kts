import kotlinx.benchmark.gradle.JvmBenchmarkTarget

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.allopen") version "2.0.21"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.12"
    id("com.shykial.aoc.inputs.downloader") version "0.0.1"
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

sourceSets {
    register("benchmarks")
}

downloadAocInputs {
    sessionCookie { projectDir.resolve("secrets/session-cookie.txt").readText() }
}

val benchmarksImplementation by configurations

dependencies {
    implementation("org.apache.commons:commons-math3:3.6.1")
    benchmarksImplementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.12")
    benchmarksImplementation(sourceSets.main.get().output + sourceSets.main.get().runtimeClasspath)
}

benchmark {
    configurations {
        (1..25).forEach { dayNumber ->
            val paddedDayNumber = dayNumber.toString().padStart(2, '0')
            val includedPattern = """.*Day$paddedDayNumber.*"""
            register("day${paddedDayNumber}") {
                include(includedPattern)
            }
            register("day${paddedDayNumber}standard") {
                include(includedPattern)
                warmups = 5
                outputTimeUnit = "ms"
                mode = "avgt"
                iterations = 5
                iterationTime = 5
                iterationTimeUnit = "sec"
            }
            register("day${paddedDayNumber}fast") {
                include(includedPattern)
                warmups = 20
                outputTimeUnit = "ms"
                iterations = 5
                iterationTime = 500
                iterationTimeUnit = "ms"
                mode = "avgt"
            }
        }
    }
    targets {
        register("benchmarks") {
            this as JvmBenchmarkTarget
            jmhVersion = "1.37"
        }
    }
}
