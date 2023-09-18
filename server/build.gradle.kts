plugins {
    id("java")
    kotlin("jvm")
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
    implementation("io.zonky.test:embedded-postgres:2.0.4")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("org.postgresql:postgresql:42.5.1")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation(project(":shared"))
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.tod87et.calculator.server.MainKt")
}