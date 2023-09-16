plugins {
    id("java")
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    application
}

group = "org.tod87et.calculator"

val exposedVersion: String by project
val ktorVersion = extra["ktor.version"] as String
val logbackVersion = extra["logback.version"] as String
val kotlinxDatetimeVersion = extra["kotlinx-datetime.version"] as String

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.zonky.test:embedded-postgres:2.0.4")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.postgresql:postgresql:42.5.1")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.tod87et.calculator.server.MainKt")
}