import gg.essential.gradle.util.noServerRunConfigs
import gg.essential.gradle.util.setJvmDefault

plugins {
    kotlin("jvm")
    id("gg.essential.multi-version")
    id("gg.essential.defaults.repo")
    id("gg.essential.defaults.java")
    id("gg.essential.defaults.loom")
    id("com.github.johnrengelman.shadow")
    id("net.kyori.blossom")
}

val mod_name: String by project
val mod_version: String by project
val mod_id: String by project

preprocess {
    vars.put("MODERN", if (project.platform.mcMinor >= 16) 1 else 0)
}

blossom {
    replaceToken("@VER@", mod_version)
    replaceToken("@NAME@", mod_name)
    replaceToken("@ID@", mod_id)
}

version = mod_version
group = "dev.isxander"
base {
    archivesName.set(mod_name)
}

tasks.compileKotlin.setJvmDefault(if (platform.mcVersion >= 11400) "all" else "all-compatibility")
loom.noServerRunConfigs()
loom {
    if (project.platform.isLegacyForge) {
        launchConfigs.named("client") {
            arg("--tweakClass", "cc.woverflow.onecore.tweaker.OneCoreTweaker")
        }
    }
}

repositories {
    maven("https://repo.woverflow.cc/")
}

val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    if (platform.isLegacyForge) {
        compileOnly ("gg.essential:essential-$platform:1933") {
            exclude(module = "keventbus")
        }

        runtimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.0.0")
        shade("cc.woverflow:onecore-tweaker:1.4.2")
    }
    if (platform.isFabric) {
        val fabricApiVersion: String by project
        val fabricLanguageKotlinVersion: String by project
        val modMenuVersion: String by project
        modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
        modImplementation("net.fabricmc:fabric-language-kotlin:$fabricLanguageKotlinVersion")
        modImplementation("com.terraformersmc:modmenu:$modMenuVersion")
    }
    val onecore = "cc.woverflow:onecore-${platform}:1.4.7"
    if (platform.isLegacyForge) {
        compileOnly(onecore)
    } else {
        modImplementation(onecore)
    }
}

tasks.processResources {
    inputs.property("id", mod_id)
    inputs.property("name", mod_name)
    val java = if (project.platform.mcMinor > 18) {
        17
    } else {if (project.platform.mcMinor >= 17) 16 else 8 }
    val compatLevel = "JAVA_${java}"
    inputs.property("java", java)
    inputs.property("java_level", compatLevel)
    inputs.property("version", mod_version)
    inputs.property("mcVersionStr", project.platform.mcVersionStr.substringBeforeLast(".") + ".x")
    filesMatching(listOf("fabric.mod.json", "mcmod.info", "mixins.${mod_id}.json")) {
        expand(mapOf(
            "id" to mod_id,
            "name" to mod_name,
            "java" to java,
            "java_level" to compatLevel,
            "version" to mod_version,
            "mcVersionStr" to project.platform.mcVersionStr
        ))
    }
}

tasks.remapJar {
    archiveClassifier.set("nodeps")
}

tasks.jar {
    manifest {
        attributes(mapOf(
            "ModSide" to "CLIENT",
            "TweakOrder" to "0",
            "TweakClass" to "cc.woverflow.onecore.tweaker.OneCoreTweaker",
            "ForceLoadAsMod" to true
        ))
    }
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        remapJar.orNull?.let { from(it.archiveFile) }
        configurations = listOf(shade)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    assemble.orNull?.dependsOn(shadowJar)
}