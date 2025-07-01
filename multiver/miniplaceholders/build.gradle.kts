plugins {
    id("sr.base-logic")
}

dependencies {
    compileOnly(projects.skinsrestorerShared)

    compileOnly("net.kyori:adventure-text-minimessage:4.23.0")
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:2.3.0")
}
