@file:Suppress("SpellCheckingInspection")

import org.leavesmc.LeavesPluginJson.Load.BEFORE
import org.leavesmc.leavesPluginJson
import xyz.jpenilla.runtask.service.DownloadsAPIService
import xyz.jpenilla.runtask.service.DownloadsAPIService.Companion.registerIfAbsent

plugins {
    java
    kotlin("jvm")
    alias(libs.plugins.leavesweightUserdev)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.runPaper)
    alias(libs.plugins.resourceFactory)
}

group = "cn.xor7.xiaohei"
version = "1.0.0-SNAPSHOT"

val pluginJson = leavesPluginJson {
    main = "cn.xor7.xiaohei.ccb.CrashCreateBroadcastPlugin"
    authors.add("MC_XiaoHei")
    description = "Update suppression crash will create configurable broadcast message"
    website = "https://github.com/MC-XiaoHei/CrashCreateBroadcast"
    foliaSupported = false
    apiVersion = libs.versions.leavesApi.extractMCVersion()
    features.required.add("update_suppression_event")
    dependencies.server(
        name = "CommandAPI",
        joinClasspath = true,
        load = BEFORE,
    )
}

val runServerPlugins = runPaper.downloadPluginsSpec {
    modrinth("commandapi", "epl0dnHR") // 10.1.2-Mojmap
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://repo.leavesmc.org/releases/") {
        name = "leavesmc-releases"
    }
    maven("https://repo.leavesmc.org/snapshots/") {
        name = "leavesmc-snapshots"
    }
    mavenLocal()
}

sourceSets {
    main {
        resourceFactory {
            factories(pluginJson.resourceFactory())
        }
    }
}

dependencies {
    apply `plugin dependencies`@{
        // TODO: your plugin deps here
    }

    apply `api and server source`@{
        compileOnly(libs.leavesApi)
        paperweight.devBundle(libs.leavesDevBundle)
    }
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    runServer {
        downloadsApiService.set(leavesDownloadApiService())
        downloadPlugins.from(runServerPlugins)
        minecraftVersion(libs.versions.leavesApi.extractMCVersion())
        systemProperty("file.encoding", Charsets.UTF_8.name())
    }

    withType<JavaCompile>().configureEach {
        options.encoding = Charsets.UTF_8.name()
        options.forkOptions.memoryMaximumSize = "6g"

        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }

    shadowJar {
        archiveFileName = "${project.name}-${version}.jar"
    }

    build {
        dependsOn(shadowJar)
    }
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

fun Provider<String>.extractMCVersion(): String {
    val versionString = this.get()
    val regex = Regex("""^(1\.\d+(?:\.\d+)?)""")
    return regex.find(versionString)?.groupValues?.get(1)
        ?: throw IllegalArgumentException("Cannot extract mcVersion from $versionString")
}

fun leavesDownloadApiService(): Provider<out DownloadsAPIService> = registerIfAbsent(project) {
    downloadsEndpoint = "https://api.leavesmc.org/v2/"
    downloadProjectName = "leaves"
    buildServiceName = "leaves-download-service"
}