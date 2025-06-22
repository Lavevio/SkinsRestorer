plugins {
    id("sr.base-logic")
}

dependencies {
    testFixturesApi(project(":skinsrestorer-shared", "shadow"))

    testImplementation("org.bstats:bstats-base:3.1.0")

    testImplementation("org.testcontainers:testcontainers:1.21.2")
    testImplementation("org.testcontainers:mariadb:1.21.2")
    testImplementation("org.testcontainers:junit-jupiter:1.21.2")

    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.17")
}
