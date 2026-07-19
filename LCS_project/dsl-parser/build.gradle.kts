plugins {
    id("java-library")
    id("org.javacc.javacc") version "4.0.1"
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

dependencies {
    // same as add("javacc", "org.javacc.core:8.1.0") using invoce() operator
    "javacc"("org.javacc:core:8.1.0")
    "javacc"("org.javacc.generator:java:8.1.0")

    testImplementation(kotlin("test"))
    // testImplementation("junit:junit:${libs.junit}")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

/**
 * finetune generated code directories
 */
val rootDirectory: String = "generated/sources/javacc/main"
val packageStructure: String = "at/fhooe/sail/android/dsl_parser/generated"
val generatedSourceRoot =
    layout.buildDirectory.dir(rootDirectory)
val generatedJavaccDir =
    layout.buildDirectory.dir("$rootDirectory/$packageStructure")
/*
 * Configure JavaCC task
 */
tasks.named("compileJavacc") {
    setProperty(
        "inputDirectory",
        file("src/main/javacc")
    )
    setProperty(
        "outputDirectory",
        generatedJavaccDir.get().asFile
    )
}

// show valid code in Android view
sourceSets {
    main {
        java {
            srcDirs(
                "src/main/java",
                generatedSourceRoot
            )
        }
        resources {
            srcDirs("src/main/javacc")
        }
    }
}

val compileJavacc by tasks.existing
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(compileJavacc)
}