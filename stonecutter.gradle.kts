plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.4-fabric" /* [SC] DO NOT EDIT */

fun chiseledTask(task : String, group : String) {
    val name = "chiseled${task.toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}"

    stonecutter registerChiseled tasks.register(name, stonecutter.chiseled) {
        versions { _, version -> version.project.endsWith("neoforge") }
        this.group = group
        ofTask(task)
        dependsOn("Pre${name.toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}")
    }

    stonecutter registerChiseled tasks.register("Pre${name.toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}", stonecutter.chiseled) {
        versions { _, version -> version.project.endsWith("fabric") }
        this.group = group
        ofTask(task)
    }
}


chiseledTask("buildAndCollect", "project")
chiseledTask("publishMods", "publishing")
chiseledTask("publishMavenPublicationToMavenLocal", "publishing")
chiseledTask("publishMavenPublicationToImideRepository", "publishing")