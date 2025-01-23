@file:Suppress("UnstableApiUsage")

plugins {
    java

    // Architectury
    id("dev.architectury.loom") version "1.9.+"
	id("architectury-plugin") version "3.4-SNAPSHOT"
	id("io.freefair.lombok") version "8.11"

	// Indra and spotless
	id("com.diffplug.spotless") version "7.0.2"
	id("net.kyori.indra.licenser.spotless") version "3.1.3"
	id("net.kyori.indra.git") version "3.1.3"

	// Publishing
    id("me.modmuss50.mod-publish-plugin") version "0.8.+"
	`maven-publish`
}

class ModData {
	val id = property("mod.id").toString()
	val name = property("mod.name")
	val version = property("mod.version")
	val group = property("mod.group").toString()
	val description = property("mod.description")
	val source = property("mod.source")
	val issues = property("mod.issues")
	val license = property("mod.license").toString()
	val modrinth = property("mod.modrinth")
	val curseforge = property("mod.curseforge")
	val kofi = property("mod.kofi")
	val modrinthId = property("modrinthId").toString()
	val curseforgeId = property("curseforgeId").toString()
	val githubProject = property("githubProject").toString()

    val isAlpha = "alpha" in version.toString()
    val isBeta = "beta" in version.toString()
}

class Dependencies {
	val modmenuVersion = property("deps.modmenu_version")
	val yaclVersion = property("deps.yacl_version")
    val fapiVersion = property("deps.fapi_version")
}

class LoaderData(private val project: Project) {
    val loader = project.loom.platform.get().name.lowercase()

     val isFabric = loader == "fabric"
     val isNeoforge = loader == "neoforge"

    fun getVersion() : String = if (isNeoforge) {
        project.findProperty("deps.neoforge").toString()
    } else {
        project.property("deps.fabric_loader").toString()
    }

    fun neoforge(container: () -> TaskContainer) {
        if(isNeoforge) container.invoke()
    }

    fun fabric(container: () -> TaskContainer) {
        if(isFabric) container.invoke()
    }
}

class McData {
	val version = property("mod.mc_version")
	val dep = property("mod.mc_dep")
	val targets = property("mod.mc_targets").toString().split(", ")
}

val mc = McData()
val mod = ModData()
val deps = Dependencies()
val loader = LoaderData(project)

// Project stuff
version = "${mod.version}+${mc.version}-${loader.loader}"

group = mod.group
base { archivesName.set(mod.id) }

stonecutter.const("fabric", loader.isFabric)
stonecutter.const("neoforge", loader.isNeoforge)

loom {
	silentMojangMappingsLicense()

	runConfigs.all {
		ideConfigGenerated(stonecutter.current.isActive)
		runDir = "../../run"
	}

	// runConfigs.remove(runConfigs["server"])
}

repositories {
    fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
    strictMaven("https://cursemaven.com", "curse.maven")
    strictMaven("https://thedarkcolour.github.io/KotlinForForge/", "thedarkcolour")
    maven("https://maven.parchmentmc.org") // Parchment
	maven("https://maven.isxander.dev/releases") // YACL
	maven("https://maven.terraformersmc.com") // Mod Menu
	maven("https://maven.neoforged.net/releases") // NeoForge
	mavenCentral()
}

dependencies {
	minecraft("com.mojang:minecraft:${mc.version}")
//
//	@Suppress("UnstableApiUsage")
//	mappings(loom.layered {
//		// Mojmap mappings
//		officialMojangMappings()
//
//		// Parchment mappings (it adds parameter mappings & javadoc)
//		optionalProp("deps.parchment_version") {
//            parchment("org.parchmentmc.data:parchment-${property("mod.mc_version")}:$it@zip")
//		}
//	})
//
//
//	if (loader.isFabric) {
//		modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
//        if (mc.version == "1.21.3") modImplementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+1.21.2-${loader.loader}") // YACL 1.21.2 is the same as 1.21.3
//        else modImplementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+${mc.version}-${loader.loader}")
//		modImplementation("com.terraformersmc:modmenu:${deps.modmenuVersion}")
//	} else if (loader.isNeoforge) {
//		"neoForge"("net.neoforged:neoforge:${findProperty("deps.neoforge")}")
//        if (mc.version == "1.21.3") implementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+1.21.2-${loader.loader}") // YACL 1.21.2 is the same as 1.21.3
//        else implementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+${mc.version}-${loader.loader}") {
//            isTransitive = false
//        }
//	}
}

loader.fabric {
    dependencies {
        modImplementation("net.fabricmc:fabric-loader:${loader.getVersion()}")
        // Mod Menu
        modImplementation("com.terraformersmc:modmenu:${deps.modmenuVersion}")
        // YACL
        // quite annoyingly, puts backwards compatable versions (1.21 and 1.21.1) the LOWER version (but not all the time????)
        // effected versions: 1.21.1 (1.21), 1.21.3 (1.21.2)
        when (mc.version) {
            "1.21.1" -> modImplementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+1.21-${loader.loader}")
            "1.21.3" -> modImplementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+1.21.2-${loader.loader}")
            else -> modImplementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+${mc.version}-${loader.loader}")
        }

        // Fabric API
        modImplementation("net.fabricmc.fabric-api:fabric-api:${deps.fapiVersion}")

        mappings(loom.layered {
            officialMojangMappings()

            optionalProp("deps.parchment_version") {
                parchment("org.parchmentmc.data:parchment-${mc.version}:$it@zip")
            }
        })
    }

    fabricApi {
        configureDataGeneration {
            modId = mod.id
        }
    }

    tasks {

    }
}

loader.neoforge {
    dependencies {
        mappings(loom.layered {
            officialMojangMappings()

            optionalProp("deps.parchment_version") {
                parchment("org.parchmentmc.data:parchment-${mc.version}:$it@zip")
            }
        })
        "neoForge"("net.neoforged:neoforge:${loader.getVersion()}")

        // YACL
        // quite annoyingly, puts backwards compatable versions (1.21 and 1.21.1) the LOWER version (but not all the time????)
        // effected versions: 1.21.1 (1.21), 1.21.3 (1.21.2)
        when (mc.version) {
            "1.21.1" -> modImplementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+1.21-${loader.loader}")
            "1.21.3" -> modImplementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+1.21.2-${loader.loader}")
            else -> modImplementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+${mc.version}-${loader.loader}")
        }
    }

   tasks {}
}

java {
	withSourcesJar()
    val java = if (stonecutter.eval(mc.version.toString(), ">=1.20.5"))
        JavaVersion.VERSION_21
    else
        JavaVersion.VERSION_17
	sourceCompatibility = java
	targetCompatibility = java
}

tasks {
	processResources {
		val props = buildMap {
			put("id", mod.id)
			put("name", mod.name)
			put("version", mod.version)
			put("mcdep", mc.dep)
			put("description", mod.description)
			put("source", mod.source)
			put("issues", mod.issues)
			put("license", mod.license)
			put("modrinth", mod.modrinth)
			put("curseforge", mod.curseforge)
			put("kofi", mod.kofi)
			put("modmenu_version", deps.modmenuVersion)
			put("yacl_version", deps.yaclVersion)

			if (loader.isNeoforge) {
				put("forgeConstraint", findProperty("modstoml.forge_constraint"))
			}
			if (mc.version == "1.20.1" || mc.version == "1.20.4") {
				put("forge_id", loader.loader)
			}
		}

		props.forEach(inputs::property)

		if (loader.isFabric) {
			filesMatching("fabric.mod.json") { expand(props) }
			exclude(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml"))
		}

		if (loader.isNeoforge) {
			if (mc.version == "1.20.4") {
				filesMatching("META-INF/mods.toml") { expand(props) }
				exclude("fabric.mod.json", "META-INF/neoforge.mods.toml")
			} else {
				filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
				exclude("fabric.mod.json", "META-INF/mods.toml")
			}
		}
	}

	register("format") {
		group = "formatting"

		description = "Formats the source code according to the project style."
		dependsOn("spotlessApply")
	}

    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.get().archiveFile)
        into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
        dependsOn("build")
    }

	spotless {
		java {
			target("src/**/*.java")
			googleJavaFormat()
			formatAnnotations()
		}
	}

    clean {
        delete(file(rootProject.file("build")))
    }

	publishMods {
        displayName.set("ChatHistoryPlus ${mod.version} for MC ${mc.version} (${loader.loader})")

        if (providers.environmentVariable("DRY_RUN").getOrNull() != null) {
            dryRun.set(true)
            println("DRY_RUN is set, publishing will not be performed.")
        }

        file.set(remapJar.get().archiveFile)

        val modVersion: String = mod.version.toString()

        version.set(modVersion)

		if (loader.isFabric) modLoaders.add("fabric")
		if (loader.isNeoforge) modLoaders.add("neoforge")

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


		modrinth {
			projectId = mod.modrinthId
            accessToken = providers.environmentVariable("MODRINTH_TOKEN")
			minecraftVersions.addAll(mc.targets)

            if (loader.isFabric) requires("fabric-api", "modmenu")
			requires("yacl")
		}
		curseforge {
			projectId = mod.curseforgeId
            accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
			minecraftVersions.addAll(mc.targets)

            if (loader.isFabric) requires("fabric-api", "modmenu")
			requires("yacl")

		}

		github {
			repository = mod.githubProject
            accessToken = providers.environmentVariable("GITHUB_TOKEN")
			commitish.set("main")
		}
	}

   extensions.configure<PublishingExtension> {
       repositories {
           maven {
               name = "imide"
               url = uri("https://maven.imide.xyz/releases")
               credentials(PasswordCredentials::class)
               authentication {
                   create<BasicAuthentication>("basic")
               }
           }
       }
       publications {
           create<MavenPublication>("maven") {
               groupId = "${mod.group}.${mod.id}"
               artifactId = "${mod.id}-${loader.loader}"
               version = "${mod.version}+${mc.version}"

           }
       }
   }
//	publishing {
//		publications {
//			create<MavenPublication>("mod") {
//				groupId = mod.group
//				artifactId = mod.id
//                version = version
//			}
//		}
//
//		repositories {
//            val username =
//                "IMIDE_MAVEN_USER".let { providers.environmentVariable(it).orNull ?: providers.gradleProperty(it) }
//                    .toString()
//            val password = "IMIDE_MAVEN_PASS".let { providers.environmentVariable(it).orNull ?: providers.gradleProperty(it) }.toString()
//
//            if (username.isNotBlank() && password.isNotBlank()) {
//                maven("https://maven.imide.xyz/releases/") {
//                    name = "imideReleases"
//                    credentials {
//                        this.username = username
//                        this.password = password
//                    }
//                    authentication {
//                        create<BasicAuthentication>("basic")
//                    }
//                }
//
//                maven("https://maven.imide.xyz/snapshots/") {
//                    name = "imideSnapshots"
//                    credentials {
//                        this.username = username
//                        this.password = password
//                    }
//                    authentication {
//                        create<BasicAuthentication>("basic")
//                    }
//                }
//            } else {
//                println("No username or password for maven.imide.xyz provided.")
//            }
//		}
//	}
}

if (stonecutter.current.isActive) {
	rootProject.tasks.register("buildActive") {
		group = "project"
		dependsOn(tasks.named("build"))
	}
}
@Suppress("TYPE_MISMATCH", "UNRESOLVED_REFERENCE")
fun <T> optionalProp(property: String, block: (String) -> T?): T? =
	findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)

fun isPropDefined(property: String): Boolean {
	return property(property)?.toString()?.isNotBlank() == true
}

