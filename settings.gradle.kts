pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/snapshots") {
            name = "KikuGie Snapshots"
        }
    }

    val fabricLoomVersion: String by settings
    plugins {
        id("net.fabricmc.fabric-loom-remap") version fabricLoomVersion
        id("net.fabricmc.fabric-loom") version fabricLoomVersion
        // id("com.github.johnrengelman.shadow") version "8.1.1"
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.3"
}

rootProject.name = "BTETerraRenderer"
include(":common")
include(":terraplusplus")
include(":draco")
include(":mcconnector")
include(":ogc3dtiles")

include(":core")

// Single-version build target
//include(":forge1.12.2")
//include(":forge1.18.2")

stonecutter {
    val ciSingleBuild: String? = System.getenv("CI_SINGLE_BUILD")
    if (ciSingleBuild != null) {
        val split = ciSingleBuild.split(":")
        create(rootProject) {
            version(split[0], split[1]).buildscript(split[2])
        }
    } else {
        create(rootProject, file("versions.json"))
    }
}
