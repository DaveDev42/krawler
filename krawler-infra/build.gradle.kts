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
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-webflux")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-actuator")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-data-redis-reactive")

    implementation(group = "com.fasterxml.jackson.core", name = "jackson-annotations")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind")
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin")
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-parameter-names")
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310")
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jdk8")

}
