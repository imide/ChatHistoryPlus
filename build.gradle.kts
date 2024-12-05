plugins {
	id("dev.architectury.loom") version "1.7.+"
	id("io.freefair.lombok") version "8.11"

	// Indra and spotless
	id("com.diffplug.spotless") version "7.0.0.BETA4"
	id("net.kyori.indra.licenser.spotless") version "3.1.3"
	id("net.kyori.indra.git") version "3.1.3"

	// Publishing
    id("me.modmuss50.mod-publish-plugin")
	`maven-publish`
	`java-library`
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
	val devauthVersion = property("deps.devauth_version")
}

class LoaderData {
	val loader = loom.platform.get().name.lowercase()
	val isFabric = loader == "fabric"
	val isNeoforge = loader == "neoforge"
}

class McData {
	val version = property("mod.mc_version")
	val dep = property("mod.mc_dep")
	val targets = property("mod.mc_targets").toString().split(", ")
}

val mc = McData()
val mod = ModData()
val deps = Dependencies()
val loader = LoaderData()

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

	runConfigs.remove(runConfigs["server"])
}

repositories {
	maven("https://maven.parchmentmc.org") // Parchment
	maven("https://maven.isxander.dev/releases") // YACL
	maven("https://thedarkcolour.github.io/KotlinForForge") // Kotlin for Forge - required by YACL
	maven("https://maven.terraformersmc.com") // Mod Menu
	maven("https://maven.neoforged.net/releases") // NeoForge
	maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") // DevAuth
	mavenCentral()
}

dependencies {
	minecraft("com.mojang:minecraft:${mc.version}")

	@Suppress("UnstableApiUsage")
	mappings(loom.layered {
		// Mojmap mappings
		officialMojangMappings()

		// Parchment mappings (it adds parameter mappings & javadoc)
		optionalProp("deps.parchment_version") {
            if (mc.version == "1.21.3" || mc.version == "1.21.4") parchment("org.parchmentmc.data:parchment-1.21:$it@zip") // TODO: remove when parchment 1.21.3/4
			else parchment("org.parchmentmc.data:parchment-${property("mod.mc_version")}:$it@zip")
		}
	})

	modRuntimeOnly("me.djtheredstoner:DevAuth-${loader.loader}:${deps.devauthVersion}")

	if (loader.isFabric) {
		modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
        if (mc.version == "1.21.3") modImplementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+1.21.2-${loader.loader}") // YACL 1.21.2 is the same as 1.21.3
        else modImplementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+${mc.version}-${loader.loader}")
		modImplementation("com.terraformersmc:modmenu:${deps.modmenuVersion}")
	} else if (loader.isNeoforge) {
		"neoForge"("net.neoforged:neoforge:${findProperty("deps.neoforge")}")
        if (mc.version == "1.21.3") implementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+1.21.2-${loader.loader}") // YACL 1.21.2 is the same as 1.21.3
        else implementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+${mc.version}-${loader.loader}") {
            isTransitive = false
        }
	}
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

	register("releaseMod") {
		group = "publishing"

		dependsOn("publishMods")
		dependsOn("publish")

        if (!project.publishMods.dryRun.get()) {
            dependsOn("publish")
        }
	}

	register("format") {
		group = "formatting"

		description = "Formats the source code according to the project style."
		dependsOn("spotlessApply")
	}

	spotless {
		java {
			target("src/**/*.java")
			googleJavaFormat()
			formatAnnotations()
		}
	}

	publishMods {
        from(rootProject.publishMods)
        dryRun.set(rootProject.publishMods.dryRun)

        file.set(remapJar.get().archiveFile)

		if (loader.isFabric) modLoaders.add("fabric")
		if (loader.isNeoforge) modLoaders.add("neoforge")


		modrinth {
			projectId = mod.modrinthId
			accessToken = providers.environmentVariable("MODRINTH_TOKEN")
			minecraftVersions.addAll(mc.targets)

			if (loader.isFabric) requires("fabric-api") else null
			requires("modmenu")
			requires("yacl")
		}
		curseforge {
			projectId = mod.curseforgeId
			accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
			minecraftVersions.addAll(mc.targets)

			if (loader.isFabric) requires("fabric-api") else null

			requires("modmenu")
			requires("yacl")

		}

		github {
			repository = mod.githubProject
			accessToken = providers.environmentVariable("GITHUB_TOKEN")
			commitish.set("main")
		}
	}

	publishing {
		publications {
			create<MavenPublication>("mod") {
				groupId = mod.group
				artifactId = mod.id
				version = mod.version as String?
			}
		}

		repositories {
            val username = "IMIDE_MAVEN_USER".let { providers.environmentVariable(it).orNull ?: providers.gradleProperty(it)}.toString()
            val password = "IMIDE_MAVEN_PASS".let { providers.environmentVariable(it).orNull ?: providers.gradleProperty(it) }.toString()

            if (username.isNotBlank() && password.isNotBlank()) {
                maven("https://maven.imide.xyz/releases/") {
                    name = "imideReleases"
                    credentials {
                        this.username = username
                        this.password = password
                    }
                    authentication {
                        create<BasicAuthentication>("basic")
                    }
                }

                maven("https://maven.imide.xyz/snapshots/") {
                    name = "imideSnapshots"
                    credentials {
                        this.username = username
                        this.password = password
                    }
                    authentication {
                        create<BasicAuthentication>("basic")
                    }
                }
            } else {
                println("No username or password for maven.imide.xyz provided.")
            }
		}
	}
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
