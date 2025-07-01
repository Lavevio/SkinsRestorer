plugins {
    id("sr.base-logic")
    id("com.gradleup.shadow")
}

dependencies {
    api(projects.skinsrestorerBuildData)
    api(projects.skinsrestorerApi)
    implementation(projects.skinsrestorerScissors)

    api("com.google.code.gson:gson:2.13.1")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.4") {
        exclude("com.github.waffle", "waffle-jna")
    }

    api("com.github.SkinsRestorer:ConfigMe:beefdbdf7e")
    api("ch.jalu:injector:1.0") {
        exclude("javax.annotation")
    }

    api("org.incendo:cloud-annotations:2.0.0")
    annotationProcessor("org.incendo:cloud-annotations:2.0.0")
    api("org.incendo:cloud-processors-requirements:1.0.0-SNAPSHOT")
    api("org.incendo:cloud-processors-cooldown:1.0.0-SNAPSHOT")
    api("org.incendo:cloud-brigadier:2.0.0-SNAPSHOT")
    api("org.incendo:cloud-translations-core:1.0.0-SNAPSHOT")
    api("org.incendo:cloud-minecraft-extras:2.0.0-SNAPSHOT")
    api("org.incendo:cloud-translations-minecraft-extras:1.0.0-SNAPSHOT")

    implementation("org.bstats:bstats-base:3.1.0") {
        isTransitive = false
    }

    compileOnly("org.geysermc.floodgate:api:2.2.2-SNAPSHOT")

    api(libs.bundles.adventure.shared)
}

tasks {
    shadowJar {
        relocate("net.kyori", "net.skinsrestorer.shadow.kyori")
    }
}
