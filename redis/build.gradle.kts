plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.kover")
    id("maven-publish")
    id("signing")
}

val kotestVersion = "5.4.1"
dependencies {
    implementation(project(":api"))
    implementation(kotlin("stdlib"))
    implementation("xyz.haff:siths:0.11.2")

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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                packaging = "jar"
                name.set(project.name)
                description.set("Redis-backed cache implementation with Me-Kachis! interfaces")

                url.set("https://github.com/huuff/mekachis")
                scm {
                    connection.set("scm:git:git://github.com/huuff/mekachis.git")
                    developerConnection.set("scm:git:git@github.com:huuff/mekachis.git")
                    url.set("https://github.com/huuff/mekachis/tree/master")
                }

                licenses {
                    license {
                        name.set("WTFPL - Do What The Fuck You Want To Public License")
                        url.set("http://www.wtfpl.net")
                    }
                }

                developers {
                    developer {
                        name.set("Francisco Sánchez")
                        email.set("haf@protonmail.ch")
                        organizationUrl.set("https://github.com/huuff")
                    }
                }
            }
        }
    }
}


signing {
    sign(publishing.publications["mavenJava"])
}