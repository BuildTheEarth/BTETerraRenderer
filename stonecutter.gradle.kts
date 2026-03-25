import java.io.ByteArrayOutputStream

plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "fabric26.1"

subprojects {
    apply(plugin = "java")
    // apply(plugin = "maven-publish")

    apply(from = rootProject.file("common.gradle.kts"))
}

tasks.register<Exec>("gitSubmoduleUpdate") {
    group = "other"
    description = "Updates submodules"

    commandLine("git", "submodule", "update", "--init")

    val stdout = ByteArrayOutputStream()
    standardOutput = stdout
    doLast {
        println("Submodule update command output: ")
        if (stdout.size() > 0) println(stdout.toString())
        else println("(none)")
    }
}