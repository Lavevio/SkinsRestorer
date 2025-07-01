plugins {
    id("sr.base-logic")
}

dependencies {
    compileOnly(projects.skinsrestorerShared)
    runtimeOnly(project(":skinsrestorer-shared", "shadow"))
    implementation(projects.multiver.bukkit.shared)

    compileOnly("org.bukkit:craftbukkit:1.7.10-R0.1-SNAPSHOT") {
        isTransitive = false
    }
}
