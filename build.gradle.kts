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
    benchmarksImplementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.10")
    benchmarksImplementation(sourceSets.main.get().output + sourceSets.main.get().runtimeClasspath)
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}

benchmark {
    targets {
        register("benchmarks") {
            this as JvmBenchmarkTarget
            jmhVersion = "1.37"
        }
    }
}
