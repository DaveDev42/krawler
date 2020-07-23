plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring").apply(true)
    id("org.jetbrains.kotlin.plugin.allopen").apply(true)
    id("org.jetbrains.kotlin.plugin.noarg").apply(true)
    id("org.jetbrains.kotlin.plugin.jpa").apply(true)
    id("org.springframework.boot").apply(true)
    id("io.spring.dependency-management").apply(true)
}

val springShellVersion: String by project

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    launchScript()
    mainClassName = "dave.dev.krawler.cli.KrawlerCliApplicationKt"
}

dependencies {

    implementation(project(":krawler-infra"))
    implementation(project(":krawler-service"))

    implementation(group = "org.springframework.boot", name = "spring-boot-starter")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter")
    implementation(group = "org.springframework.shell", name = "spring-shell-starter", version = springShellVersion)
}
