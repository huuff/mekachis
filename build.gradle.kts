plugins {
    kotlin("jvm") version "1.6.21" apply false
    id("org.jetbrains.kotlinx.kover") version "0.5.0" apply false
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("signing")
}

allprojects {
    group = "xyz.haff.mekachis"
    version = "0.2.0"

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

        project.extensions.findByType(JavaPluginExtension::class.java)?.apply {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(properties["sonatype.user"] as String)
            password.set(properties["sonatype.password"] as String)
        }
    }
}

tasks.wrapper {
    gradleVersion = "7.4"
}