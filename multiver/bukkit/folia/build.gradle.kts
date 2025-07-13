plugins {
    id("sr.shadow-logic")
}

dependencies {
    compileOnly(projects.skinsrestorerShared)
    runtimeOnly(project(":skinsrestorer-shared", "shadow"))
    implementation(projects.multiver.bukkit.shared)

    compileOnly(libs.adventure.api)
    compileOnly(libs.folia.api) {
        isTransitive = false
    }
}
