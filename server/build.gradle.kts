plugins {
    id("java")
    kotlin("jvm")
    application
}

group = "org.tod87et"

val exposedVersion: String by project

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.slf4j:slf4j-simple:2.0.3")
    implementation("org.slf4j:slf4j-api:2.0.3")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.postgresql:postgresql:42.5.1")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.tod87et.MainKt")
}