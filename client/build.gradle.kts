import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.tod87et.calculator"
version = "1.0.0"

val coroutinesVersion get() = rootProject.extra["kotlinx.coroutines.version"]
val serializationVersion get() = rootProject.extra["kotlinx.serialization.version"]
val kTorVersion get() = rootProject.extra["ktor.version"]
val kotlinxDatetimeVersion = extra["kotlinx-datetime.version"] as String

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "org.tod87et.calculator.client.MainKt"
    }
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("io.ktor:ktor-client-core:$kTorVersion")
    implementation("io.ktor:ktor-client-okhttp:$kTorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$kTorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$kTorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
    implementation(project(":shared"))
    implementation("org.slf4j:slf4j-nop:2.0.5")
}

compose.desktop {
    application {
        mainClass = "org.tod87et.calculator.client.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "client"
            packageVersion = version.toString()
        }
    }
}
