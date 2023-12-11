import kotlinx.benchmark.gradle.JvmBenchmarkTarget
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.allopen") version "1.9.21"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.10"
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

sourceSets {
    register("benchmarks")
}

val benchmarksImplementation by configurations

dependencies {
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("com.squareup.okhttp3:okhttp-coroutines:5.0.0-alpha.11")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    benchmarksImplementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.10")
    benchmarksImplementation(sourceSets.main.get().output + sourceSets.main.get().runtimeClasspath)
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
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
