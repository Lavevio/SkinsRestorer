plugins {
    id("sr.shadow-logic")
}

dependencies {
    compileOnly(projects.skinsrestorerShared)
    runtimeOnly(project(":skinsrestorer-shared", "shadow"))
    implementation(projects.multiver.bukkit.shared)

    compileOnly("net.kyori:adventure-api:4.23.0")
    compileOnly("dev.folia:folia-api:1.19.4-R0.1-SNAPSHOT") {
        isTransitive = false
    }
}
