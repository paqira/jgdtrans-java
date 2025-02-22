/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java library project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.7/userguide/building_java_projects.html in the Gradle documentation.
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`
    signing

    alias(libs.plugins.spotless)
}

group = "io.github.paqira"
base.archivesName.set(rootProject.name)
version = "0.1.0"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    options.memberLevel = JavadocMemberLevel.PUBLIC
    title = "${rootProject.name} $version API"
    (options as StandardJavadocDocletOptions).header = "${rootProject.name} $version"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = rootProject.name
            from(components["java"])
            pom {
                name = "jgdtrans"
                description = "Coordinate Transformer by Gridded Correction Parameter (par file)"
                url = "https://github.com/paqira/jgdtrans-java"
                licenses {
                    license {
                        name = "Apache-2.0"
                        url = "https://spdx.org/licenses/Apache-2.0.html"
                    }
                }
                developers {
                    developer {
                        id = "paqira"
                        name = "Kentaro Tatsumi"
                        email = "paqira.2019@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/paqira/jgdtrans-java.git"
                    developerConnection = "scm:git:ssh://github.com:paqira/jgdtrans-java.git"
                    url = "https://github.com/paqira/jgdtrans-java/tree/main"
                }
            }
        }
    }
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.10.2")
        }
    }
}

spotless {
    // optional: limit format enforcement to just the files changed by this feature branch
    // ratchetFrom("origin/main")

    format("misc") {
        // define the files to apply `misc` to
        target(
            "README.md",
            ".gitignore",
            "**/libs.versions.toml",
        )

        // define the steps to apply to those files
        trimTrailingWhitespace()
        indentWithTabs() // or spaces. Takes an integer argument if you don't like 4
        endWithNewline()
    }

    kotlin {
        target("**/*.kts")
        ktlint()
    }

    java {
        // don't need to set target, it is inferred from java

        // apply a specific flavor of google-java-format
        googleJavaFormat()
        // fix formatting of type annotations
        formatAnnotations()
        // make sure every file has the following copyright header.
        // optionally, Spotless can set copyright years by digging
        // through git history (see "license" section below)
        licenseHeader(
            """/*
 * Copyright $""" + "YEAR" + """ Kentaro Tatsumi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */""",
        )
    }
}
