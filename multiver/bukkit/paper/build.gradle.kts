plugins {
    id("sr.base-logic")
}

dependencies {
    compileOnly(projects.skinsrestorerShared)
    runtimeOnly(project(":skinsrestorer-shared", "shadow"))
    implementation(projects.multiver.bukkit.shared)

    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
}
