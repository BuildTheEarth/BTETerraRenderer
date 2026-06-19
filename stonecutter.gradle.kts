plugins {
    id("dev.kikugie.stonecutter")
}

val ciSingleBuild: String? = System.getenv("CI_SINGLE_BUILD")
if (ciSingleBuild != null) {
    stonecutter active ciSingleBuild.split(":")[0]
} else {
    stonecutter active "fabric26.2"
}

subprojects {
    apply(plugin = "java")
    // apply(plugin = "maven-publish")

    apply(plugin = "common")
}
