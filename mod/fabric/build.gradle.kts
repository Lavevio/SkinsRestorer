plugins {
    id("dev.architectury.loom") version "1.11-SNAPSHOT"
    id("sr.base-logic")
    id("com.gradleup.shadow")
}

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
    if (name == "developmentFabric") {
        extendsFrom(common)
    }
}

dependencies {
    minecraft("net.minecraft:minecraft:${rootProject.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")

    // Architectury API. This is optional, and you can comment it out if you don't need it.
    modImplementation("dev.architectury:architectury-fabric:${rootProject.property("architectury_api_version")}")

    common(project(path = ":skinsrestorer-mod-common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(path = ":skinsrestorer-mod-common", configuration = "transformProductionFabric"))
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
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
