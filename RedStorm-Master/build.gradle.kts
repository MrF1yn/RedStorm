plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.mrflyn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:4.3.1") // Redis support
    implementation("com.squareup.okhttp3:okhttp:4.9.3") // HTTP client
}

application {
    mainClass.set("dev.mrflyn.redstorm_master.Main")
}

tasks.shadowJar {
    archiveFileName.set("redstorm_master.jar")
    manifest {
        attributes["Main-Class"] = "dev.mrflyn.redstorm_master.Main"
    }
}
tasks.build {
    dependsOn(tasks.shadowJar)
}