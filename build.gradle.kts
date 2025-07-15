import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.RunConfigurationContainer

plugins {
    id("java-library")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
    id("com.gtnewhorizons.retrofuturagradle") version "1.4.0"
}

group = "me.iwareq.testtask"
val modId = "testtaskmod"
version = "1.0.0"

// Set the toolchain version to decouple the Java we run Gradle with from the Java used to compile and run the mod
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        // Azul covers the most platforms for Java 8 toolchains, crucially including MacOS arm64
        vendor.set(JvmVendorSpec.AZUL)
    }
    // Generate sources and javadocs jars when building and publishing
    withSourcesJar()
    withJavadocJar()
}

// Most RFG configuration lives here, see the JavaDoc for com.gtnewhorizons.retrofuturagradle.MinecraftExtension
minecraft {
    mcVersion.set("1.7.10")

    mcpMappingChannel.set("stable")
    mcpMappingVersion.set("12")

    // Username for client run configurations
    username.set("IWareQ")

    // Generate a field named VERSION with the mod version in the injected Tags class
    injectedTags.put("VERSION", project.version)
    injectedTags.put("MOD_ID", modId)

    // If you need the old replaceIn mechanism, prefer the injectTags task because it doesn't inject a javac plugin.
    // tagReplacementFiles.add("RfgExampleMod.java")

    // Enable assertions in the mod's package when running the client or server
    extraRunJvmArguments.add("-ea:${project.group}")

    // If needed, add extra tweaker classes like for mixins.
    // extraTweakClasses.add("org.spongepowered.asm.launch.MixinTweaker")

    // Exclude some Maven dependency groups from being automatically included in the reobfuscated runs
    groupsToExcludeFromAutoReobfMapping.addAll("com.diffplug", "com.diffplug.durian", "net.industrial-craft")
}

tasks.injectTags.configure {
    outputClassName.set("${project.group}.Tags")
}

tasks.processResources.configure {
    val projVersion = project.version.toString()
    inputs.property("version", projVersion)

    filesMatching("mcmod.info") {
        expand(
            mapOf(
                "modId" to modId,
                "modVersion" to projVersion
            )
        )
    }
}

repositories {
    maven {
        name = "OvermindDL1 Maven"
        url = uri("https://gregtech.overminddl1.com/")
    }
    maven {
        name = "GTNH Maven"
        url = uri("https://nexus.gtnewhorizons.com/repository/public/")
    }

    maven {
        name = "Curse Maven"
        url = uri("https://cursemaven.com")
    }
}

dependencies {
    api("com.github.GTNewHorizons:NotEnoughItems:2.3.39-GTNH:dev")

    api(rfg.deobf("curse.maven:botania-225643:2283837"))
    api(rfg.deobf("curse.maven:baubles-227083:2224857"))

    api(rfg.deobf("curse.maven:avaritia-233785:2519595"))
    api(rfg.deobf("curse.maven:blood-magic-224791:2264826"))

    // Для теста перекачки крови трубами
    api(rfg.deobf("curse.maven:ender-io-64578:4671445"))
    api(rfg.deobf("curse.maven:endercore-231868:4671288"))

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
        inheritOutputDirs = true // Fix resources in IJ-Native runs
    }

    project {
        this.withGroovyBuilder {
            "settings" {
                "runConfigurations" {
                    val self = this.delegate as RunConfigurationContainer
                    self.add(Gradle("1. Run Client").apply {
                        setProperty("taskNames", listOf("runClient"))
                    })
                    self.add(Gradle("2. Run Server").apply {
                        setProperty("taskNames", listOf("runServer"))
                    })
                    self.add(Gradle("3. Run Obfuscated Client").apply {
                        setProperty("taskNames", listOf("runObfClient"))
                    })
                    self.add(Gradle("4. Run Obfuscated Server").apply {
                        setProperty("taskNames", listOf("runObfServer"))
                    })
                }
                "compiler" {
                    val self = this.delegate as org.jetbrains.gradle.ext.IdeaCompilerConfiguration
                    afterEvaluate {
                        self.javac.moduleJavacAdditionalOptions = mapOf(
                            (project.name + ".main") to tasks.compileJava.get().options.compilerArgs.joinToString(" ") { '"' + it + '"' }
                        )
                    }
                }
            }
        }
    }
}

tasks.processIdeaSettings.configure {
    dependsOn(tasks.injectTags)
}
