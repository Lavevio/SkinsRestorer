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
    neoForge()
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
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    named("developmentNeoForge").get().extendsFrom(common)
}

dependencies {
    minecraft("net.minecraft:minecraft:${rootProject.libs.versions.minecraft.mod.get()}")
    mappings(loom.officialMojangMappings())

    neoForge(libs.neoforge)

    // Architectury API
    modImplementation(libs.architectury.neoforge)

    common(project(path = ":skinsrestorer-mod:common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(path = ":skinsrestorer-mod:common", configuration = "transformProductionNeoForge"))

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

    filesMatching("META-INF/neoforge.mods.toml") {
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
