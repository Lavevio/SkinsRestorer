plugins {
    id("sr.base-logic")
}

dependencies {
    implementation(project(":skinsrestorer-shared", "shadow"))

    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT") {
        isTransitive = false
    }
}
