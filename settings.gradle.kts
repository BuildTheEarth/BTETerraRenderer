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
    id("dev.kikugie.stonecutter") version "0.8.3"
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
    create(rootProject) {
        fun register(version: String, vararg loaders: String) {
            for (loader in loaders) {
                version("$loader$version", version).buildscript = "build.$loader.gradle.kts"
            }
        }

        register("1.21.11", "fabric")
        register("1.21.10", "fabric")
        // register("1.21.9", "fabric") // https://github.com/FabricMC/fabric-api/issues/4902
        register("1.21.6", "fabric") // Also compatible with 1.21.7-1.21.8
        register("1.21.5", "fabric")
        register("1.21.4", "fabric")
        register("1.21.2", "fabric") // Also compatible with 1.21.3
        register("1.21", "fabric") // Also compatible with 1.21.1

        register("1.20.5", "fabric") // Also compatible with 1.20.6
        register("1.20.3", "fabric") // Also compatible with 1.20.4
        register("1.20.2", "fabric")
        register("1.20", "fabric") // Also compatible with 1.20.1

        register("1.19.4", "fabric")
        register("1.19.3", "fabric")
        register("1.19", "fabric") // Also compatible with 1.19.1 and 1.19.2

        register("1.18.1", "fabric") // Also compatible with 1.18.2
        register("1.18", "fabric")
    }
}