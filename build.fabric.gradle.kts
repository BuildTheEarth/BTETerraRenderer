import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("net.fabricmc.fabric-loom-remap") apply false
    id("net.fabricmc.fabric-loom") apply false
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

val (javaVersionInteger, javaVersionEnum) = {
    when {
        mcVersion >= "26.1-alpha" -> 25 to JavaVersion.VERSION_25
        mcVersion >= "1.20.5"     -> 21 to JavaVersion.VERSION_21
        mcVersion >= "1.18"       -> 17 to JavaVersion.VERSION_17
        else                      ->  8 to JavaVersion.VERSION_1_8
    }
}()
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
}

configurations {
    val shadowDep = maybeCreate("shadowDep")
    named("include") { extendsFrom(shadowDep) }
}

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

    if (mcVersion > "1.12") { // for T++
        "shadowDep"("lzma:lzma:0.0.1")
    }
    if (mcVersion < "1.19.4") {
        "shadowDep"("org.joml:joml:1.10.8") {
            exclude(group = "org.jetbrains", module = "annotations")
        }
    }
    if (mcVersion >= "1.19") {
        "shadowDep"("io.netty:netty-codec-http:4.1.9.Final")
        "shadowDep"("io.netty:netty-codec-http2:4.1.9.Final")
        "shadowDep"("org.apache.xmlgraphics:xmlgraphics-commons:2.9")
        "shadowDep"("org.w3c.css:sac:1.3")
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

    from(project(":core").file("src/main/resources/icon.png")) {
        into("")
    }
}

project.tasks.register<Copy>("copyBuildResultToRoot") {
    group = "build"
    description = "Copies build result into root build directory"
    from("${project.projectDir}/build/libs")
    into("${rootProject.projectDir}/build/libs")
    dependsOn("build")
}
tasks.named("build").configure { finalizedBy("copyBuildResultToRoot") }

project.tasks.register<Delete>("cleanModProjects") {
    group = "build"
    description = "Cleans mod projects"
    dependsOn("clean")
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
