plugins {
    id("dev.kikugie.stonecutter")
    id("net.kyori.indra.git")
    id("me.modmuss50.mod-publish-plugin") version "0.8.+"
}

stonecutter active "1.21.4-fabric" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("buildAllVersions", stonecutter.chiseled) {
    group = "chiseled"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("runClient", stonecutter.chiseled) {
    group = "chiseled"
    ofTask("runClient")
}

stonecutter registerChiseled tasks.register("releaseAllVersions", stonecutter.chiseled) {
    group = "chiseled"
    ofTask("releaseModVersion")
}


val releaseMod by tasks.registering {
    group = "mod"
    dependsOn("releaseAllVersions")
    dependsOn("publishMods")
}

val modVersion: String = property("mod.version").toString()
version = modVersion

val versionProjects = stonecutter.versions.map { findProject(it.project)!! }

publishMods {
    val modChangelog =
        rootProject.file("changelog.md")
            .takeIf { it.exists() }
            ?.readText()
            ?.replace("{version}", modVersion)
            ?.replace(
                "{targets}", stonecutter.versions
                    .map { it.project }
                    .joinToString(separator = "\n") { "- ${it}" })
            ?: "No changelog provided."
    changelog.set(modChangelog)

    type.set(
        when {
            "alpha" in modVersion -> ALPHA
            "beta" in modVersion -> BETA
            else -> STABLE
        }
    )
}

tasks.named("publishMods") {
    dependsOn("releaseAllVersions")
}

