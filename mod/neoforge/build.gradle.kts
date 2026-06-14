plugins {
    id("sr.base-logic")
    id("com.gradleup.shadow")
    id("dev.architectury.loom-no-remap") version "1.17.480"
}

base {
    archivesName = "SkinsRestorer-Mod-NeoForge"
}

loom {
    silentMojangMappingsLicense()

    accessWidenerPath = project(":skinsrestorer-mod-common").file("src/main/resources/skinsrestorer.accesswidener")

    neoForge {
        accessTransformer(project.file("src/main/resources/META-INF/accesstransformer.cfg"))
    }
}

val common: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

configurations {
    compileClasspath {
        extendsFrom(common)
    }
    runtimeClasspath {
        extendsFrom(common)
    }
}

configurations.configureEach {
    if (name == "developmentNeoForge") {
        extendsFrom(common)
    }
}

repositories {
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    minecraft("net.minecraft:minecraft:${rootProject.property("modMcVersion")}")

    neoForge("net.neoforged:neoforge:${rootProject.property("neoforge_version")}")

    // Cloud command framework for NeoForge
    implementation("org.incendo:cloud-neoforge:${rootProject.property("cloud_neoforge_version")}")
    include("org.incendo:cloud-neoforge:${rootProject.property("cloud_neoforge_version")}")

    common(project(path = ":skinsrestorer-mod-common")) { isTransitive = false }
    shadowBundle(
        project(
            path = ":skinsrestorer-mod-common",
        )
    ) { isTransitive = false }

    // Shared project dependencies - added to common for compile-time and shadowBundle for packaging
    setOf(
        projects.skinsrestorerShared,
        projects.multiver.miniplaceholders,
        projects.multiver.viaversion
    ).forEach {
        common(it) {
            exclude("org.slf4j")
            exclude("com.google.code.gson")
            exclude("com.google.errorprone")
        }
        shadowBundle(it) {
            exclude("org.slf4j")
            exclude("com.google.code.gson")
            exclude("com.google.errorprone")
        }
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(mapOf("version" to inputs.properties["version"]))
    }
}

tasks.shadowJar {
    val mainOutputDirectories = sourceSets.main.get().output.files.map { it.toPath().toAbsolutePath().normalize() }

    dependsOn(tasks.jar)
    from(zipTree(tasks.jar.flatMap { it.archiveFile }))
    from(rootProject.layout.projectDirectory.file("LICENSE"))
    eachFile {
        val sourcePath = file.toPath().toAbsolutePath().normalize()
        if (mainOutputDirectories.any(sourcePath::startsWith)) {
            exclude()
        }
    }

    configurations = listOf(shadowBundle)
    archiveClassifier.set("")
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
}

tasks.jar {
    archiveClassifier.set("raw")
}
