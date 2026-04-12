import java.io.ByteArrayOutputStream

plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "fabric26.1"

subprojects {
    apply(plugin = "java")
    // apply(plugin = "maven-publish")

    apply(plugin = "common")
}
