plugins {
    kotlin("jvm") version "1.6.21" apply false
    id("org.jetbrains.kotlinx.kover") version "0.5.0" apply false
}

allprojects {
    group = "xyz.haff.mekachis"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }
}

subprojects {

    afterEvaluate {
        tasks.withType<Test> {
            useJUnitPlatform()
            jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")
            finalizedBy(tasks.getByName("koverHtmlReport"))
        }
    }
}

tasks.wrapper {
    gradleVersion = "7.4"
}