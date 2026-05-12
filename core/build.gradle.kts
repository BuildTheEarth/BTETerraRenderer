dependencies {
    implementation(project(":terraplusplus"))
    implementation(project(":ogc3dtiles"))
    implementation(project(":draco"))
    implementation(project(":common"))
    implementation(project(":mcconnector"))
}

tasks.withType<ProcessResources>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    doLast {
        val r1 = Regex("#.*")
        val r2 = Regex("""([^=]+)=(.+)""")
        outputs.files.asFileTree.matching { include("**/*.lang") }.files.forEach { langFile: File ->
            val content = langFile.readText(Charsets.UTF_8).split("\n")
                .asSequence()
                .map { it.replace(r1, "") }
                .mapNotNull { r2.find(it) }
                .map { it.groupValues[1] to it.groupValues[2] }
                .map { (k, v) ->
                    """    "$k": "${v.replace("\"", "\\\\\"")}""""
                }
                .joinToString(",\n")

            val jsonFile = File(langFile.parentFile, langFile.nameWithoutExtension + ".json")
            jsonFile.writeText("{\n$content\n}", Charsets.UTF_8)
        }
    }
}
