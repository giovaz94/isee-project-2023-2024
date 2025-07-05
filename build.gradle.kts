import Utils.platform
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

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
    with(libs) {
        implementation(kotlin.stdlib)
        implementation(kotlin.stdlib.jdk8)
        implementation(javafx)
        implementation(jakta)
        implementation(bundles.coroutines)
        implementation(google.guava)
        bundles.javafx.modules.get().forEach {
            implementation("${it.module.group}:${it.module.name}:${it.versionConstraint.requiredVersion}:${platform()}")
        }
        testImplementation(bundles.kotlin.testing)
        testImplementation(bundles.testfx)
    }
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

tasks.withType<Test>().configureEach {
    testLogging {
        events(*TestLogEvent.values())
        exceptionFormat = TestExceptionFormat.FULL
    }
    useJUnitPlatform()
}

tasks.withType<Jar> {
    archiveClassifier.set(platform())
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

// Set the project version based on the git history.
gitSemVer {
    assignGitSemanticVersion()
}
