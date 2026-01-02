plugins {
    id("dev.architectury.loom") version "1.13-SNAPSHOT"
    id("sr.base-logic")
    id("com.gradleup.shadow")
}

base {
    archivesName = "SkinsRestorer-Mod-Fabric"
}

loom {
    silentMojangMappingsLicense()

    accessWidenerPath = project(":skinsrestorer-mod-common").file("src/main/resources/skinsrestorer.accesswidener")
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
    minecraft("net.minecraft:minecraft:${rootProject.property("modMcVersion")}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")

    // Bump fabric api
    modImplementation(enforcedPlatform("net.fabricmc.fabric-api:fabric-api-bom:${rootProject.property("fabric_api_version")}"))
    include(enforcedPlatform("net.fabricmc.fabric-api:fabric-api-bom:${rootProject.property("fabric_api_version")}"))

    // Fabric API. We only need it for compilation.
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")

    // Architectury API - needed for modImplementations to load
    modImplementation("dev.architectury:architectury-fabric:${rootProject.property("architectury_version")}")
    include("dev.architectury:architectury-fabric:${rootProject.property("architectury_version")}")

    // Cloud command framework for Fabric
    modImplementation("org.incendo:cloud-fabric:${rootProject.property("cloud_fabric_version")}")
    include("org.incendo:cloud-fabric:${rootProject.property("cloud_fabric_version")}")

    // Fabric permissions API
    modImplementation("me.lucko:fabric-permissions-api:${rootProject.property("fabric_permissions_api_version")}")
    include("me.lucko:fabric-permissions-api:${rootProject.property("fabric_permissions_api_version")}")

    common(project(path = ":skinsrestorer-mod-common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(":skinsrestorer-mod-common")) { isTransitive = false }

    // Shared project dependencies - added to common for compile-time and shadowBundle for packaging
    setOf(
        projects.skinsrestorerShared,
        projects.multiver.miniplaceholders,
        projects.multiver.viaversion
    ).forEach {
        implementation(it) {
            exclude("com.google.code.gson")
            exclude("com.google.errorprone")
        }
        include(it) {
            exclude("com.google.code.gson")
            exclude("com.google.errorprone")
        }
    }
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
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
}
