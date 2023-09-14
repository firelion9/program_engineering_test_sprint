plugins {
    id("java")
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
}

group = "org.tod87et"
val ktorVersion = extra["ktor.version"] as String
val logbackVersion = extra["logback.version"] as String
val kotlinxDatetimeVersion = extra["kotlinx-datetime.version"] as String

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
}

tasks.test {
    useJUnitPlatform()
}