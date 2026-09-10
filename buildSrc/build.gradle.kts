plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/snapshots") {
        name = "KikuGie Snapshots"
    }
}

dependencies {
    implementation("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:9.6.1")
    implementation("dev.kikugie:stonecutter:0.9.8")
    implementation("org.codehaus.plexus:plexus-utils:4.1.0")
}
