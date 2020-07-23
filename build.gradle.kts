import java.io.ByteArrayOutputStream

buildscript {
    val kotlinVersion by extra("1.3.72")
    val springBootVersion by extra("2.3.0.RELEASE")
    val springDependencyManagementPluginVersion by extra("1.0.8.RELEASE")
    repositories {
        mavenCentral()
        jcenter()
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
        maven("https://plugins.gradle.org/m2/")
        maven("https://repo.spring.io/plugin-release")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
        classpath("io.spring.gradle:dependency-management-plugin:$springDependencyManagementPluginVersion")
    }
}

val kotlinVersion by extra("1.3.72")
val springBootVersion by extra("2.3.0.RELEASE")
val springCloudVersion by extra("Finchley.RELEASE")
val springShellVersion by extra("2.0.1.RELEASE")
val springDependencyManagementPluginVersion by extra("1.0.8.RELEASE")

val funktionaleVersion by extra("1.2")
val kotlinLoggingVersion by extra("1.8.0.1")

val junitBomVersion by extra("5.6.0")
val assertkVersion by extra("0.22")
val mockkVersion by extra("1.10.0")
val springfoxVersion by extra("3.0.0")

group = "dave.dev"
version = "0.0.1-SNAPSHOT"

plugins {
    id("java")
}

allprojects {

    group = rootProject.group
    version = rootProject.version

    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.spring.dependency-management")

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    configurations.all {
        resolutionStrategy {
            failOnVersionConflict()
        }
    }

    repositories {
        mavenCentral()
        jcenter()
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
    }

    tasks.register("version") {
        doLast {
            println(project.version)
        }
    }
}

subprojects {

    configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports(
            delegateClosureOf<io.spring.gradle.dependencymanagement.dsl.ImportsHandler> {
                mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion") {
                    bomProperty("kotlin.version", kotlinVersion)
                }
                mavenBom("org.junit:junit-bom:$junitBomVersion")
            }
        )
        dependencies {
            dependency("com.google.guava:guava:23.4-jre")
        }
    }

    tasks.named<Test>("test") {
        useJUnitPlatform {

        }
    }

    dependencies {
        implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8")
        implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect")
        implementation(group = "io.github.microutils", name = "kotlin-logging", version = kotlinLoggingVersion)

        implementation(group = "org.funktionale", name = "funktionale-all", version = funktionaleVersion)

        implementation(group = "io.projectreactor", name = "reactor-core")
        implementation(group = "io.projectreactor.netty", name = "reactor-netty")
        implementation(group = "io.projectreactor.addons", name = "reactor-extra")
        implementation(group = "io.projectreactor.kotlin", name = "reactor-kotlin-extensions")

        testImplementation(group = "org.junit.jupiter", name = "junit-jupiter")
        testRuntimeOnly(group = "org.junit.platform", name = "junit-platform-launcher")
        testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine")

        implementation(group = "org.apache.logging.log4j", name = "log4j-to-slf4j")
        implementation(group = "org.slf4j", name = "jul-to-slf4j")
        runtimeOnly(group = "ch.qos.logback", name = "logback-classic")
        runtimeOnly(group = "ch.qos.logback", name = "logback-core")

        testImplementation(group = "org.jetbrains.kotlin", name = "kotlin-test")
        testImplementation(group = "org.jetbrains.kotlin", name = "kotlin-test-junit")
        testImplementation(group = "com.willowtreeapps.assertk", name = "assertk-jvm", version = assertkVersion)
        testImplementation(group = "io.mockk", name = "mockk", version = mockkVersion) {
            exclude(group = "org.objenesis", module = "objenesis")
        }
        testImplementation(group = "io.projectreactor", name = "reactor-test")
        testRuntimeOnly(group = "net.bytebuddy", name = "byte-buddy")
        testRuntimeOnly(group = "net.bytebuddy", name = "byte-buddy-agent")

        if (project.name != "krawler-infra") {
            testImplementation(
                project(":krawler-infra")
                    .dependencyProject
                    .sourceSets
                    .test.get()
                    .output
            )
        }
    }
}

val ktlint by configurations.creating

dependencies {
    ktlint("com.pinterest:ktlint:0.37.2")
}

tasks.register<JavaExec>("ktlint") {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("krawler-*/**/*.kt")
    tasks.getByName("check").dependsOn(this)
}

tasks.register<JavaExec>("ktlintFormat") {
    group = "verification"
    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("-F", "krawler-*/**/*.kt")
    tasks.getByName("check").dependsOn(this)
}

tasks.register<Exec>("gitChanges") {
    description = "Get all git changed files"
    commandLine = listOf("/usr/bin/env", "bash", "-c", "git status -s --no-renames | grep '.*\\.kt\$' | awk 'match(\$0, /[A-Z] (.*)/) { print(\$2) }'")
    standardOutput = ByteArrayOutputStream()
    doLast {
        val result = standardOutput.toString()
        ext.set("gitChanges", result.split("\n"))
        print(result)
    }
}

tasks.register<JavaExec>("ktlintFormatChanges") {
    doFirst {
        args = listOf("-F") + (ext["gitChanges"] as List<String>)
    }
    group = "verification"
    description = "Fix Kotlin code style deviations for git changed files."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("-F", "krawler-*/**/*.kt")
    this.dependsOn(tasks.getByName("gitChanges"))
}
