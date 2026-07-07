import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("net.fabricmc.fabric-loom-remap") apply false
    id("net.fabricmc.fabric-loom") apply false
    id("com.gradleup.shadow")
    // id("maven-publish")
}

val mcVersion = sc.current.parsed
val isUnobfuscated = mcVersion >= "26.1-alpha"

if (isUnobfuscated) {
    apply(plugin = "net.fabricmc.fabric-loom")
}
else {
    apply(plugin = "net.fabricmc.fabric-loom-remap")
}

val (javaVersionInteger, javaVersionEnum) = when {
    mcVersion >= "26.1-alpha" -> 25 to JavaVersion.VERSION_25
    mcVersion >= "1.20.5"     -> 21 to JavaVersion.VERSION_21
    mcVersion >= "1.18"       -> 17 to JavaVersion.VERSION_17
    else                      ->  8 to JavaVersion.VERSION_1_8
}
println("Java version set to $javaVersionEnum for $project")

tasks.withType<JavaCompile>().configureEach {
    options.release = javaVersionInteger
}

java {
    withSourcesJar()
    sourceCompatibility = javaVersionEnum
    targetCompatibility = javaVersionEnum
}

configure<LoomGradleExtensionAPI> {
    splitEnvironmentSourceSets()

    mods {
        create("bteterrarenderer") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }

    val accessWidener = rootProject.file("src/main/resources/${project.findProperty("aw") ?: if (isUnobfuscated) "emptyofficial.accesswidener" else "empty.accesswidener"}")
    if (accessWidener.exists()) {
        accessWidenerPath = accessWidener
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    if (isUnobfuscated) {
        enabled = false; // Use shadowJar directly instead
    }
}

/*configurations { // Use shadowJar instead of JIJ
    val shadowDep = maybeCreate("shadowDep")
    named("include") { extendsFrom(shadowDep) }
}*/

dependencies {
    // Fabric deps
    "minecraft"("com.mojang:minecraft:${sc.current.version}")

    if (!isUnobfuscated) {
        val loom = project.extensions.getByType<LoomGradleExtensionAPI>()
        "mappings"(loom.officialMojangMappings())
    }

    val myModImplementation = if (isUnobfuscated) "implementation" else "modImplementation"

    myModImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabricLoaderVersion")}")

    // Fabric API (bundle)
    myModImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabricVersion")}")
}

if (isUnobfuscated) {
    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>().configureEach {
        from(sourceSets["client"].output) // Required otherwise client source set outputs are not included
        archiveClassifier.set(null /*main artifact*/)
    }
} else {
    tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
        destinationDirectory.set(layout.buildDirectory.dir("devlibs"))
        archiveClassifier.set("remapped")
    }

    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>().configureEach {
        val remapJarTask = tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar")
        dependsOn(remapJarTask)

        // Clear pre-added build/classes/java/main and build/resources/main
        // We need the remapped classes and AWs
        val mainSpecField = AbstractCopyTask::class.java
            .getDeclaredField("mainSpec").apply { isAccessible = true }
        val mainSpec = mainSpecField.get(this)
        val sourcePathsField = org.gradle.api.internal.file.copy.DefaultCopySpec::class.java
            .getDeclaredField("sourcePaths").apply { isAccessible = true }
        val sourcePaths = sourcePathsField.get(mainSpec) as ConfigurableFileCollection
        sourcePaths.setFrom(emptyList<Any>())

        from(zipTree(remapJarTask.flatMap { it.archiveFile }))
        archiveClassifier.set(null /*main artifact*/)
    }
}

tasks.withType<ProcessResources>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    val resourceTargets = listOf(
        "mcmod.info",
        "META-INF/mods.toml",
        "fabric.mod.json",
        "mixins.bteterrarenderer.client.json"
    )
    val aw = project.findProperty("aw") ?: if (isUnobfuscated) "emptyofficial.accesswidener" else "empty.accesswidener"
    val replaceProperties = mapOf(
        "version" to             rootProject.property("mod_version"),
        "mcversion" to           (project.findProperty("minecraftVersion") ?: sc.current.version),
        "authors" to             rootProject.property("mod_authors"),
        "displayName" to         rootProject.property("mod_displayName"),
        "description" to         rootProject.property("mod_description"),
        "url" to                 rootProject.property("mod_url"),
        "sourceUrl" to           rootProject.property("mod_sourceUrl"),
        "discordUrl" to          rootProject.property("mod_discordUrl"),
        "credits" to             rootProject.property("mod_credits"),
        "license" to             rootProject.property("mod_license"),
        "fabricLoaderVersion" to rootProject.property("fabricLoaderVersion"),
        "javaVersionInteger" to  javaVersionInteger.toString(),
        "aw" to                  aw,
    )

    inputs.properties(replaceProperties)

    filesMatching(resourceTargets) {
        expand(replaceProperties)
    }

    filesMatching("**/*.accesswidener") {
        if (name != aw) {
            exclude()
        }
    }

    /*from(project(":core").file("src/main/resources/icon.png")) { // Already handled by shadowJar
        into("")
    }*/
}

/*afterEvaluate {
    configure<LoomGradleExtensionAPI> {
        mods {
            named("bteterrarenderer") {
                sourceSet(rootProject.project(":core").sourceSets["main"])
                sourceSet(rootProject.project(":terraplusplus").sourceSets["main"])
                sourceSet(rootProject.project(":ogc3dtiles").sourceSets["main"])
                sourceSet(rootProject.project(":draco").sourceSets["main"])
            }
        }
    }
}*/
