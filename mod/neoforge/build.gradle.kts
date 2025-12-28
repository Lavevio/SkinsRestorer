plugins {
    id("dev.architectury.loom") version "1.13-SNAPSHOT"
    id("sr.base-logic")
    id("com.gradleup.shadow")
}

base {
    archivesName = "SkinsRestorer-Mod-NeoForge"
}

loom {
    silentMojangMappingsLicense()

    accessWidenerPath = project(":skinsrestorer-mod-common").file("src/main/resources/skinsrestorer.accesswidener")

    neoForge {
        accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
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
    mappings(loom.officialMojangMappings())

    neoForge("net.neoforged:neoforge:${rootProject.property("neoforge_version")}")

    // Architectury API - needed for modImplementations to load
    modImplementation(libs.architectury.neoforge)
    include(libs.architectury.neoforge)

    // Cloud command framework for NeoForge
    modImplementation(libs.cloud.neoforge)
    include(libs.cloud.neoforge)

    common(project(path = ":skinsrestorer-mod-common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(":skinsrestorer-mod-common")) { isTransitive = false }

    // Shared project dependencies - added to common for compile-time and shadowBundle for packaging
    setOf(
        projects.skinsrestorerShared,
        projects.multiver.miniplaceholders,
        projects.multiver.viaversion
    ).forEach {
        common(it) {
            exclude("com.google.code.gson")
            exclude("com.google.errorprone")
        }
        shadowBundle(it) {
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
    configurations = listOf(shadowBundle)
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}
