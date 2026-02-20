plugins {
    id("java")
    id("io.freefair.lombok") version "9.1.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "9.3.0"
}

group = "dev.lumas.build"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.jsinco.dev/releases")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
    maven("https://maven.canvasmc.io/snapshots")
}

dependencies {
    compileOnly("net.luckperms:api:5.4")
    compileOnly("dev.lumas.lumacore:LumaCore:d56563b")
    //compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("io.canvasmc.canvas:canvas-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.nuclyon.technicallycoded.inventoryrollback:InventoryRollbackPlus:1.7.3")
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:5.0.5")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:5.0.5")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    jar {
        enabled = false
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        val shaded = "dev.lumas.build.shaded"
        relocate("eu.okaeri", "$shaded.okaeri")
        archiveClassifier.set("")
    }

    runServer {
        minecraftVersion("1.21.11")
        downloadPlugins {
            modrinth("fastasyncworldedit", "2.14.3")
        }
    }
}