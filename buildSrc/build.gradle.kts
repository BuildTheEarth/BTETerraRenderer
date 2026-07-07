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
    implementation("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:9.4.2")
    implementation("dev.kikugie:stonecutter:0.9.3")
    implementation("org.codehaus.plexus:plexus-utils:4.0.3")
}
