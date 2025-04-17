plugins {
    java
    id("com.gradleup.shadow") version "8.3.0"
}

group = "net.milkbowl.vault"
version = "1.7.3"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://nexus.hc.to/content/repositories/pub_releases/") {
        name = "nexus-repo"
    }
    maven("https://hub.spigotmc.org/nexus/content/groups/public/") {
        name = "spigot-repo" // Might be gone soon
    }
    maven("https://dev.escapecraft.com/maven/") {
        name = "escapecraft-repo"
    }
    maven("https://repo.codemc.org/repository/maven-public/") {
        name = "codemc-repo"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("net.milkbowl.vault:VaultAPI:1.7")
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 17 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.shadowJar {
    archiveClassifier.set("")

    relocate("org.bstats", "net.milkbowl.vault.metrics")
}

tasks.jar {
    archiveClassifier.set("dev")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
    filesMatching("plugin.yml") {
        expand(props)
    }
}