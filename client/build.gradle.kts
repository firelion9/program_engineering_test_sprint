import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

group = "org.tod87et.calculator"
version = "1.0.0"

val coroutinesVersion get() = rootProject.extra["kotlinx.coroutines.version"]
val serializationVersion get() = rootProject.extra["kotlinx.serialization.version"]
val kTorVersion get() = rootProject.extra["ktor.version"]
val kotlinxDatetimeVersion = extra["kotlinx-datetime.version"] as String

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
    implementation("org.tod87et.calculator:shared")
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
