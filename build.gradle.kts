import Utils.platform
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.git.semantic.versioning)
    alias(libs.plugins.shadowJar)
    application
}

group = "io.github.evasim"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.jason)
    implementation(libs.javafx)
    libs.bundles.javafx.modules.get().forEach {
        implementation("${it.module.group}:${it.module.name}:${it.versionConstraint.requiredVersion}:${platform()}")
    }
    testImplementation(libs.bundles.kotlin.testing)
    testImplementation(libs.bundles.testfx)
}

sourceSets {
    main {
        resources {
            srcDir("src/main/asl")
        }
    }
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
    }
}

tasks.withType<Test>().configureEach {
    testLogging {
        events(*TestLogEvent.values())
        exceptionFormat = TestExceptionFormat.FULL
    }
    useJUnitPlatform()
}

application {
    mainClass.set("io.github.evasim.EvaSimApp")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaExec> {
    jvmArgs = listOf(
        "--module-path",
        configurations.runtimeClasspath.get().asPath,
        "--add-modules",
        "javafx.controls,javafx.fxml",
    )
}

projectDir.walkTopDown().filter { it.extension == "mas2j" }.forEach { mas2jFile ->
    val taskName = "run${mas2jFile.nameWithoutExtension.capitalized()}Mas"
    if (!tasks.names.contains(taskName)) {
        tasks.register<JavaExec>(taskName) {
            group = "run"
            classpath = sourceSets.getByName("main").runtimeClasspath
            mainClass.set("jason.infra.centralised.RunCentralisedMAS")
            args(mas2jFile.path)
            standardInput = System.`in`
            javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
        }
    }
}

tasks.register<JavaExec>("runApp") {
    group = "run"
    description = "Execute application"
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("io.github.evasim.Main")
    args = listOf()
    standardInput = System.`in`
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
}

// Set the project version based on the git history.
gitSemVer {
    assignGitSemanticVersion()
}
