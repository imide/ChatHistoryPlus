pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		maven("https://maven.neoforged.net/releases/")
		maven("https://maven.architectury.dev")
		maven("https://maven.kikugie.dev/releases")
        mavenCentral()
        gradlePluginPortal()
	}
}

plugins {
    id("dev.kikugie.stonecutter") version "0.6.1"
    id("net.kyori.indra.git") version "3.1.3"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        fun mc(mcVersion: String, loaders: Iterable<String>) {
            for (loader in loaders) {
                vers("$mcVersion-$loader", mcVersion)
            }
        }

        mc("1.20.1", listOf("fabric"))
        mc("1.20.4", listOf("fabric"))
        mc("1.20.6", listOf("fabric"))
        mc("1.21.1", listOf("fabric", "neoforge"))
        mc("1.21.3", listOf("fabric", "neoforge"))
        mc("1.21.4", listOf("fabric", "neoforge"))
        mc("1.21.5", listOf("fabric", "neoforge"))

        vcsVersion = "1.21.5-fabric"
    }
}
rootProject.name = "ChatHistoryPlus"