plugins {
    id("dev.kikugie.stonecutter")
    id("net.kyori.indra.git")
    id("me.modmuss50.mod-publish-plugin") version "0.8.+"
}

stonecutter active "1.21.4-fabric" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "mod"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("chiseledPublishMods", stonecutter.chiseled) {
    group = "mod"
    ofTask("releaseMod")
}