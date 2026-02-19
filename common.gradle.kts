version = "${rootProject.property("mod_version")}-${project.name}"
group = rootProject.property("mod_group").toString()

extensions.configure<BasePluginExtension> {
    archivesName = rootProject.property("mod_id").toString()
}

tasks.named<JavaCompile>("compileJava") {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://maven.daporkchop.net/")
    // maven("https://repo.opencollab.dev/snapshot/")
    // maven("https://jitpack.io/")
    // maven("https://repo.elytradev.com/")
}

val modLoaderName = findProperty("modLoaderName").toString()

enum class SubprojectType(val isMod: Boolean) {
    CORE(false), FORGE(true), FABRIC(true), LIBRARY(false)
}
val subprojectType = {
    if      (modLoaderName == "core")            SubprojectType.CORE
    else if (modLoaderName.startsWith("forge"))  SubprojectType.FORGE
    else if (modLoaderName.startsWith("fabric")) SubprojectType.FABRIC
    else                                         SubprojectType.LIBRARY
}()

if (!subprojectType.isMod) {
    val (javaVersionInteger, javaVersionEnum) = 17 to JavaVersion.VERSION_17
    println("Java version set to $javaVersionEnum for $project")

    tasks.withType<JavaCompile>().configureEach {
        options.release = javaVersionInteger
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(javaVersionInteger)
        sourceCompatibility = javaVersionEnum
        targetCompatibility = javaVersionEnum
    }
}

configurations {
    val shadowDep = maybeCreate("shadowDep")
    val compileAndTestOnly = maybeCreate("compileAndTestOnly")

    named("implementation") { extendsFrom(shadowDep) }
    named("compileOnly") { extendsFrom(compileAndTestOnly) }
    named("testImplementation") { extendsFrom(compileAndTestOnly) }
}

dependencies {
    // Fix javax.annotation.Nonnull + friends
    "compileOnly"("com.google.code.findbugs:jsr305:3.0.2")
    "testCompileOnly"("com.google.code.findbugs:jsr305:3.0.2")

    if (modLoaderName != "common") "shadowDep"(project(":common"))
    if (modLoaderName != "common" && modLoaderName != "mcconnector") {
        "shadowDep"(project(":mcconnector"))
    }
    if (modLoaderName == "ogc3dtiles") {
        "shadowDep"(project(":draco"))
    }

    // Mod projects depend on core
    if (subprojectType.isMod) {
        "shadowDep"(project(":core"))
        "shadowDep"(project(":ogc3dtiles"))
        "shadowDep"(project(":draco"))
        "shadowDep"(project(":terraplusplus"))
        "shadowDep"(project(":ogc3dtiles"))
        "shadowDep"(project(":draco"))
    }

    // Shadow deps
    "shadowDep"("com.fasterxml.jackson.core:jackson-annotations:2.14.2")
    "shadowDep"("com.fasterxml.jackson.core:jackson-core:2.14.2")
    "shadowDep"("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    "shadowDep"("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
    "shadowDep"("de.javagl:jgltf-impl-v2:2.0.3")
    "shadowDep"("de.javagl:jgltf-model:2.0.3")
    "shadowDep"("net.daporkchop.lib:common:0.5.7-SNAPSHOT") { exclude(group = "io.netty") }
    "shadowDep"("net.daporkchop.lib:binary:0.5.7-SNAPSHOT") { exclude(group = "io.netty") }
    "shadowDep"("net.daporkchop.lib:unsafe:0.5.7-SNAPSHOT")
    "shadowDep"("org.apache.xmlgraphics:batik-anim:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-awt-util:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-bridge:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-codec:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-constants:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-css:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-dom:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-ext:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-gvt:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-i18n:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-parser:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-script:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-svg-dom:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-transcoder:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-util:1.17")
    "shadowDep"("org.apache.xmlgraphics:batik-xml:1.17")
    "shadowDep"("xml-apis:xml-apis-ext:1.3.04")
    "shadowDep"("org.osgeo:proj4j:0.1.0")
    "shadowDep"("org.yaml:snakeyaml:1.33")

    // Compile/test-only deps
    "compileAndTestOnly"("org.apache.logging.log4j:log4j-core:2.20.0")
    "compileAndTestOnly"("org.apache.commons:commons-lang3:3.12.0")
    "compileAndTestOnly"("commons-codec:commons-codec:1.16.0")
    "compileAndTestOnly"("com.google.guava:guava:31.1-jre")
    "compileAndTestOnly"("io.netty:netty-all:4.1.9.Final")
    "compileAndTestOnly"("lzma:lzma:0.0.1")
    if (!subprojectType.isMod) {
        "compileAndTestOnly"("org.joml:joml:1.10.8")
    }

    // Lombok
    "compileOnly"("org.projectlombok:lombok:1.18.32")
    "testCompileOnly"("org.projectlombok:lombok:1.18.32")
    "annotationProcessor"("org.projectlombok:lombok:1.18.32")

    // Tests
    "testImplementation"("junit:junit:4.13.2")
    "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.8.2")
    "testImplementation"("org.apache.logging.log4j:log4j-core:2.20.0")
    "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    "testRuntimeOnly"("junit:junit:4.13.2")
}

if (!subprojectType.isMod) {
    tasks.named("test").configure { dependsOn(rootProject.tasks.named("gitSubmoduleUpdate")) }
    project.tasks.register("buildNonModProjects") {
        group = "build"
        description = "Builds non-mod projects.\nThis is because fabric requires dependency jars to be present before building."
        dependsOn("build")
    }
}