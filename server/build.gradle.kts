plugins {
    id("java")
    kotlin("jvm")
}

group = "org.tod87et"
val ktorVersion = extra["ktor.version"] as String
val kotlinxCli = extra["kotlinx-cli.version"] as String

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:$kotlinxCli")
}

tasks.test {
    useJUnitPlatform()
}