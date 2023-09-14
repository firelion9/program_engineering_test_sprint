plugins {
    id("java")
    kotlin("jvm")
    application
}

group = "org.tod87et.calculator"

val exposedVersion: String by project

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.zonky.test:embedded-postgres:2.0.4")

    implementation("org.slf4j:slf4j-nop:2.0.3")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.postgresql:postgresql:42.5.1")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.tod87et.calculator.server.MainKt")
}