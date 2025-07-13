plugins {
    id("sr.base-logic")
}

dependencies {
    compileOnly(projects.skinsrestorerShared)
    runtimeOnly(project(":skinsrestorer-shared", "shadow"))
    implementation(projects.multiver.bungee.shared)

    // Keep import's on older version for SkinApplierBungeeOld
    compileOnly(libs.bungeecord.api) {
        isTransitive = false
    }
    compileOnly(libs.bungeecord.proxy.old)
}
