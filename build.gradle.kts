
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "top.focess.mc.mi"
version = "1.0.1"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    commonMainImplementation(group = "top.focess", name = "focess-util", version = "1.1.12")
    commonMainImplementation(group = "top.focess", name = "focess-scheduler", version = "1.1.6")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "top.focess.mc.mi.ui.MainKt"
        jvmArgs.add("--add-opens")
        jvmArgs.add("java.base/java.io=ALL-UNNAMED")
        nativeDistributions {
            linux {
                iconFile.set(project.file("src/jvmMain/resources").resolve("logo.png"))
            }
            macOS {
                iconFile.set(project.file("src/jvmMain/resources").resolve("logo.icns"))
            }
            windows {
                iconFile.set(project.file("src/jvmMain/resources").resolve("logo.ico"))
            }
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MINuclearSimulator"
            packageVersion = project.version.toString()
        }
    }
}
