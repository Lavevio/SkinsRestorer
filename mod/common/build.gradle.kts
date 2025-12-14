plugins {
    id("dev.architectury.loom") version "1.13-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("io.freefair.lombok")
}

architectury {
    minecraft = libs.versions.minecraft.mod.get()
}

architectury {
    common(listOf("fabric", "neoforge"))
}

loom {
    accessWidenerPath = file("src/main/resources/skinsrestorer.accesswidener")
    silentMojangMappingsLicense()
}

dependencies {
    minecraft("net.minecraft:minecraft:${rootProject.libs.versions.minecraft.mod.get()}")
    mappings(loom.officialMojangMappings())

    // We depend on Fabric Loader here to use the Fabric @Environment annotations,
    // which get remapped to the correct annotations on each platform.
    // Do NOT use other classes from Fabric Loader.
    modImplementation(libs.fabric.loader)

    // Architectury API
    modImplementation(libs.architectury)

    // SkinsRestorer shared code
    api(projects.skinsrestorerShared)
    api(projects.multiver.miniplaceholders)
    api(projects.multiver.viaversion)
}
