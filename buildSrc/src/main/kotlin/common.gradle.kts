import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter

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
    exclusiveContent {
        forRepository {
            maven("https://maven.daporkchop.net/")
        }
        filter {
            includeGroup("net.daporkchop.lib")
        }
    }
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

val mcVersion = if (subprojectType.isMod)
    extensions.getByType<dev.kikugie.stonecutter.build.StonecutterBuildExtension>().current.parsed
else
    null

if (mcVersion == null) {
    val (javaVersionInteger, javaVersionEnum) = 17 to JavaVersion.VERSION_17
    println("Java version set to $javaVersionEnum for $project")

    tasks.withType<JavaCompile>().configureEach {
        options.release = javaVersionInteger
    }

    extensions.configure<JavaPluginExtension> {
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
    if (mcVersion != null) {
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
    "shadowDep"("org.apache.xmlgraphics:batik-transcoder:1.17")
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
    if (mcVersion == null) {
        "compileAndTestOnly"("org.joml:joml:1.10.8")
    }

    // Lombok
    "compileOnly"("org.projectlombok:lombok:1.18.44")
    "testCompileOnly"("org.projectlombok:lombok:1.18.44")
    "annotationProcessor"("org.projectlombok:lombok:1.18.44")

    // Tests
    "testImplementation"("junit:junit:4.13.2")
    "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.8.2")
    "testImplementation"("org.apache.logging.log4j:log4j-core:2.20.0")
    "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    "testRuntimeOnly"("junit:junit:4.13.2")

    if (mcVersion != null) {
        if (mcVersion > "1.12") { // for T++
            "shadowDep"("lzma:lzma:0.0.1")
        }
        if (mcVersion < "1.19.4") {
            "shadowDep"("org.joml:joml:1.10.8") {
                exclude(group = "org.jetbrains", module = "annotations")
            }
        }
        if (mcVersion >= "1.19") {
            "shadowDep"("io.netty:netty-codec-http:4.1.9.Final") {
                isTransitive = false
            }
            "shadowDep"("io.netty:netty-codec-http2:4.1.9.Final") {
                isTransitive = false
            }
            "shadowDep"("org.apache.xmlgraphics:xmlgraphics-commons:2.9")
            "shadowDep"("org.w3c.css:sac:1.3")
        }
    }
}

if (mcVersion != null) {
    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>().configureEach {
        configurations = listOf(project.configurations.getByName("shadowDep"))

        val dependencyReplacements = mutableMapOf(
            "com.fasterxml.jackson"      to "jackson",
            "de.javagl.jgltf"            to "jgltf",
            "net.daporkchop.lib"         to "porklib",
            "org.apache.commons.io"      to "apache.commons.io",
            "org.apache.commons.logging" to "apache.commons.logging",
            "org.apache.xmlgraphics"     to "xmlgraphics",
            "org.apache.batik"           to "batik",
            "org.apache.xmlcommons"      to "xmlcommons",
            "org.osgeo.proj4j"           to "proj4j",
            "org.w3c.dom.smil"           to "w3cdom.smil",
            "org.w3c.dom.svg"            to "w3cdom.svg",
            "org.yaml.snakeyaml"         to "snakeyaml",
        )
        if (mcVersion > "1.12") {
            dependencyReplacements.putAll(
                mapOf(
                    // "LZMA" to "lzma" // Unusual package name, got NoClassDefFoundError
                )
            )
        }
        if (mcVersion < "1.19.4") {
            dependencyReplacements.putAll(
                mapOf(
                    "org.joml" to "joml"
                )
            )
        }
        if (mcVersion >= "1.19") {
            dependencyReplacements.putAll(
                mapOf(
                    "io.netty.handler.codec.http"  to "netty.http",
                    "io.netty.handler.codec.rtsp"  to "netty.rtsp",
                    "io.netty.handler.codec.spdy"  to "netty.spdy",
                    "io.netty.handler.codec.http2" to "netty.http2",
                    "org.w3c.css.sac"              to "w3ccss.sac",
                )
            )
        }

        val dependenciesLocation = "${rootProject.property("mod_group")}.${rootProject.property("mod_id")}.dep"
        val dependencyReplacementsPrefixed = dependencyReplacements.mapValues { "$dependenciesLocation.${it.value}" }

        dependencyReplacementsPrefixed.forEach { relocate(it.key, it.value) }
        transform(ReplacePropertyContentTransformer::class.java) {
            replacements = dependencyReplacementsPrefixed
        }

        /*minimize {
            exclude(dependency("org.apache.xmlgraphics:batik-css:.*")) // XMLResourceDescriptor.java:75
            exclude(dependency("org.w3c.css:sac:.*")) // XMLResourceDescriptor.java:122
        }*/

        exclude("**/module-info.class")
        exclude("license/**/*")
        exclude("about_files/**/*")
        exclude("about.html")
        exclude("plugin.properties")
        exclude("kotlin/**/*")
        exclude("javax/xml/**/*")
        exclude("org/w3c/dom/bootstrap/**/*")
        exclude("org/w3c/dom/css/**/*")
        exclude("org/w3c/dom/events/**/*")
        exclude("org/w3c/dom/html/**/*")
        exclude("org/w3c/dom/ls/**/*")
        exclude("org/w3c/dom/ranges/**/*")
        exclude("org/w3c/dom/stylesheets/**/*")
        exclude("org/w3c/dom/traversal/**/*")
        exclude("org/w3c/dom/views/**/*")
        exclude("org/w3c/dom/xpath/**/*")
        exclude("org/w3c/dom/*")
        exclude("org/xml/sax/**/*")
    }
}

class ReplacePropertyContentTransformer : com.github.jengelman.gradle.plugins.shadow.transformers.ResourceTransformer {

    @Input
    lateinit var replacements: Map<String, String>

    private val pathMap = hashMapOf<String, String>()

    override fun canTransformResource(element: FileTreeElement): Boolean {
        return element.relativePath.pathString.endsWith(".properties")
    }

    override fun transform(context: com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext) {
        val buffer = ByteArrayOutputStream()
        org.codehaus.plexus.util.IOUtil.copy(context.inputStream, buffer)
        context.inputStream.close()
        var content = buffer.toString("UTF-8")

        this.replacements.forEach { (k, v) ->
            if (k in content) {
                content = content.replace(k, v)
            }
        }
        this.pathMap[context.path] = content
    }

    override fun hasTransformedResource(): Boolean = !this.pathMap.isEmpty()

    override fun modifyOutputStream(os: org.apache.tools.zip.ZipOutputStream, preserveFileTimestamps: Boolean) {
        val zipWriter = OutputStreamWriter(os, "UTF-8")
        this.pathMap.forEach { (path, content) ->
            val entry = org.apache.tools.zip.ZipEntry(path)
            if (!preserveFileTimestamps) {
                entry.time = 0L
            }
            os.putNextEntry(entry)
            org.codehaus.plexus.util.IOUtil.copy(ByteArrayInputStream(content.toByteArray(Charsets.UTF_8)), zipWriter)
            zipWriter.flush()
            os.closeEntry()
        }
        this.pathMap.clear()
    }

    override fun getName(): String = "ReplacePropertyContentTransformer"
}

if (mcVersion != null) {
    project.tasks.register<Copy>("copyBuildResultToRoot") {
        group = "build"
        description = "Copies build result into root build directory"
        from(layout.buildDirectory.dir("libs")) {
            include("${rootProject.property("mod_id")}-$version.jar")
        }
        into(rootProject.layout.buildDirectory.dir("libs"))
        dependsOn("build")
    }
    tasks.named("build").configure { finalizedBy("copyBuildResultToRoot") }

    project.tasks.register<Delete>("cleanModProjects") {
        group = "build"
        description = "Cleans mod projects"
        dependsOn("clean")
    }
}
