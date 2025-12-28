plugins {
    id("dev.architectury.loom") version "1.11-SNAPSHOT"
    id("sr.base-logic")
    id("com.gradleup.shadow")
}

//architectury {
//    platformSetupLoomIde()
//    neoForge()
//}

loom {
    silentMojangMappingsLicense()
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
    minecraft("net.minecraft:minecraft:${rootProject.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    "neoForge"("net.neoforged:neoforge:${rootProject.property("neoforge_version")}")

    // Architectury API. This is optional, and you can comment it out if you don't need it.
    modImplementation("dev.architectury:architectury-neoforge:${rootProject.property("architectury_api_version")}")

    common(project(path = ":skinsrestorer-mod-common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(path = ":skinsrestorer-mod-common", configuration = "transformProductionNeoForge"))
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
