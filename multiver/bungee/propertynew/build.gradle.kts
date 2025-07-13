plugins {
    id("sr.base-logic")
}

dependencies {
    implementation(projects.skinsrestorerApi)
    implementation(projects.multiver.bungee.shared)

    compileOnly(libs.bungeecord.api) {
        isTransitive = false
    }
    compileOnly(libs.bungeecord.proxy.new) {
        isTransitive = false
    }
    compileOnly(libs.bungeecord.protocol) {
        isTransitive = false
    }
}
