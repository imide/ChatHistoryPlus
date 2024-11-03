plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.3-fabric" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "chiseled"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("chiseledRunClient", stonecutter.chiseled) {
    group = "chiseled"
    ofTask("runClient")
}

stonecutter registerChiseled tasks.register("buildAllVersions", stonecutter.chiseled) {
    group = "chiseled"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("releaseAllVersions", stonecutter.chiseled) {
    group = "chiseled"
    ofTask("releaseMod")
}
