plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.5.3"
}

group = "ua.in.hft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    version.set("2021.1")
    type.set("IC") // Target IDE Platform IU - Ultimate RD - Rider

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("222.*")
    }

    signPlugin {
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
        certificateChain.set(File(System.getenv("CERTIFICATE_CHAIN") ?: "./.keys/chain.crt").readText(Charsets.UTF_8))
        certificateChain.set(File(System.getenv("PRIVATE_KEY") ?: "./.keys/private.pem").readText(Charsets.UTF_8))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
