plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring").apply(true)
    id("org.jetbrains.kotlin.plugin.allopen").apply(true)
    id("org.jetbrains.kotlin.plugin.noarg").apply(true)
    id("org.jetbrains.kotlin.plugin.jpa").apply(true)
    id("org.springframework.boot").apply(false)
    id("io.spring.dependency-management").apply(true)
}

dependencies {

    implementation(project(":krawler-domain"))
    implementation(project(":krawler-infra"))

}
