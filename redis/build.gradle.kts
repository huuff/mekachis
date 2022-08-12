plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.kover")
}

val kotestVersion = "5.4.1"
dependencies {
    implementation(project(":api"))
    implementation(kotlin("stdlib"))
    implementation("xyz.haff:siths:0.8.0")

    testImplementation(kotlin("test"))
    testImplementation("org.testcontainers:testcontainers:1.17.3")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:1.3.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("io.mockk:mockk:1.12.5")
}