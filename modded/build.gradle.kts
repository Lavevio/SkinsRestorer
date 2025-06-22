plugins {
    id("sr.base-logic")
    id("xyz.wagyourtail.unimined") version "1.3.14"
}

val main: SourceSet by sourceSets.getting
val fabric: SourceSet by sourceSets.creating
val neoforge: SourceSet by sourceSets.creating

unimined.minecraft {
    version = "1.21.6"

    mappings {
        intermediary()
        mojmap()
        parchment("1.21.5", "2025.06.15")

        devFallbackNamespace("official")
    }

    if (sourceSet == main) {
        mods {
            modImplementation {
                namespace("intermediary")
            }
        }
        runs {
            off = true
        }
    }

    runs {
        config("server") {
            standardInput = System.`in`
        }
    }

    defaultRemapJar = false
}

unimined.minecraft(fabric) {
    combineWith(main)

    fabric {
        loader("0.16.14")
    }

    defaultRemapJar = true
}

unimined.minecraft(neoforge) {
    combineWith(main)

    neoForge {
        loader("11-beta")
    }

    minecraftRemapper.config {
        // neoforge adds 1 conflict, where 2 interfaces have a method with the same name on yarn/mojmap,
        // but the method has different names in the intermediary mappings.
        // this is a conflict because they have a class that extends both interfaces.
        // this shouldn't be a problem as long as named mappings don't make the name of those 2 methods different.
        ignoreConflicts(true)
    }

    defaultRemapJar = true
}

val modImplementation: Configuration by configurations.getting
val fabricModImplementation: Configuration by configurations.getting
val neoforgeModImplementation: Configuration by configurations.getting
val fabricInclude: Configuration by configurations.getting
val neoforgeInclude: Configuration by configurations.getting
val fabricImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val neoforgeImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

dependencies {
    implementation(projects.skinsrestorerShared)
    fabricInclude(projects.skinsrestorerShared)
    neoforgeInclude(projects.skinsrestorerShared)

    compileOnly("org.spongepowered:mixin:0.8.7")

    modImplementation("net.kyori:adventure-platform-mod-shared-fabric-repack:6.4.0")
    modImplementation("dev.architectury:architectury:17.0.6")

    fabricModImplementation("net.kyori:adventure-platform-fabric:6.4.0")
    fabricInclude("net.kyori:adventure-platform-fabric:6.4.0")
    fabricModImplementation("dev.architectury:architectury-fabric:17.0.6")
    fabricInclude("dev.architectury:architectury-fabric:17.0.6")
    fabricModImplementation("org.incendo:cloud-fabric:2.0.0-SNAPSHOT")
    fabricInclude("org.incendo:cloud-fabric:2.0.0-SNAPSHOT")
    fabricModImplementation("me.lucko:fabric-permissions-api:0.4.0")
    fabricInclude("me.lucko:fabric-permissions-api:0.4.0")

    neoforgeModImplementation("net.kyori:adventure-platform-neoforge:6.4.0")
    neoforgeInclude("net.kyori:adventure-platform-neoforge:6.4.0")
    neoforgeModImplementation("dev.architectury:architectury-neoforge:17.0.6")
    neoforgeInclude("dev.architectury:architectury-neoforge:17.0.6")
    neoforgeModImplementation("org.incendo:cloud-neoforge:2.0.0-SNAPSHOT")
    neoforgeInclude("org.incendo:cloud-neoforge:2.0.0-SNAPSHOT")
}

tasks.getByName<ProcessResources>("processFabricResources") {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.getByName<ProcessResources>("processNeoforgeResources") {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to project.version)
    }
}
