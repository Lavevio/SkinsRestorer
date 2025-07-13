plugins {
    id("sr.platform-logic")
    alias(libs.plugins.runwaterfall)
}

base {
    archivesName = "SkinsRestorer-Bungee"
}

dependencies {
    compileOnly(projects.skinsrestorerShared)
    runtimeOnly(project(":skinsrestorer-shared", "shadow"))
    implementation(projects.multiver.bungee.shared)
    implementation(projects.multiver.bungee.propertyold)
    implementation(projects.multiver.bungee.propertynew)
    testImplementation(testFixtures(projects.test))

    compileOnly(libs.bungeecord.api) {
        isTransitive = false
    }
    compileOnly(libs.bungeecord.proxy.new)

    implementation(libs.bstats.bungeecord)
    implementation(libs.cloud.bungee)

    implementation(libs.adventure.bungeecord)
}

tasks {
    runWaterfall {
        version(libs.versions.runwaterfallversion.get())
    }
}

tasks {
    shadowJar {
        relocate("net.kyori", "net.skinsrestorer.shadow.kyori")
    }
}
