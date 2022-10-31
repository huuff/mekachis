plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.kover")
    id("maven-publish")
    id("signing")
}

dependencies {
    api(project(":api"))
    implementation(kotlin("stdlib"))
}

// TODO: From parent project
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                packaging = "jar"
                name.set(project.name)
                description.set("General-purpose interfaces for abstracting cache systems")

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