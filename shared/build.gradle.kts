plugins {
    id("java")
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    application
}

group = "org.tod87et.calculator"
val ktorVersion = extra["ktor.version"] as String
val kotlinxDatetimeVersion = extra["kotlinx-datetime.version"] as String


dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
}

tasks.test {
    useJUnitPlatform()
}
