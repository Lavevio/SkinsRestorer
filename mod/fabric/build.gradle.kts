plugins {
    id("dev.architectury.loom") version "1.13-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("io.freefair.lombok")
    id("com.gradleup.shadow")
}

architectury {
    minecraft = libs.versions.minecraft.mod.get()
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath = file("../common/src/main/resources/skinsrestorer.accesswidener")
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
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    named("developmentFabric").get().extendsFrom(common)
}

dependencies {
    minecraft("net.minecraft:minecraft:${rootProject.libs.versions.minecraft.mod.get()}")
    mappings(loom.officialMojangMappings())

    modImplementation(libs.fabric.loader)

    // Fabric API
    modImplementation(enforcedPlatform(libs.fabric.api.bom))

    // Architectury API
    modImplementation(libs.architectury.fabric)

    // Cloud command framework
    // modImplementation(libs.cloud.fabric)
    // include(libs.cloud.fabric)

    // Fabric Permissions API
    // modImplementation(libs.fabric.permissions.api)
    // include(libs.fabric.permissions.api)

    common(project(path = ":skinsrestorer-mod:common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(path = ":skinsrestorer-mod:common", configuration = "transformProductionFabric"))

    // SkinsRestorer shared code for shadow bundling
    shadowBundle(projects.skinsrestorerShared) {
        exclude("com.google.code.gson")
        exclude("com.google.errorprone")
    }
    shadowBundle(projects.multiver.miniplaceholders) {
        exclude("com.google.code.gson")
        exclude("com.google.errorprone")
    }
    shadowBundle(projects.multiver.viaversion) {
        exclude("com.google.code.gson")
        exclude("com.google.errorprone")
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
    inputFile = tasks.shadowJar.get().archiveFile
}
