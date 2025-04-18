import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.git.semantic.versioning)
    alias(libs.plugins.shadowJar)
}

group = "io.github.evasim"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.jason)
    testImplementation(libs.bundles.kotlin.testing)
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

// Set the project version based on the git history.
gitSemVer {
    assignGitSemanticVersion()
}
