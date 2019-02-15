import com.jfrog.bintray.gradle.BintrayExtension
import java.util.Date

val bintrayUsername: String? by project
val bintrayApiKey: String? by project

val mongodbBsonVersion: String by project
val kotlinxVersion: String by project
val mongodbDriverReactivestreamsVersion: String by project
val kotlinTestVersion: String by project
val mockkVersion: String by project
val flapdoodleVersion: String by project
val mongodbDriverSyncVersion: String by project

group = "com.sparetimedevs"
version = "0.0.1-EXPERIMENTAL-uq3z0ks"

plugins {
    `build-scan`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "1.3.20"
    id("com.jfrog.bintray") version "1.8.4"
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service" 
    termsOfServiceAgree = "yes"

    publishAlways() 
}

repositories {
    jcenter()
}

dependencies {
	api("org.mongodb:bson:$mongodbBsonVersion")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$kotlinxVersion")
    implementation("org.mongodb:mongodb-driver-reactivestreams:$mongodbDriverReactivestreamsVersion")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:$kotlinTestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:$flapdoodleVersion")
    testImplementation("org.mongodb:mongodb-driver-sync:$mongodbDriverSyncVersion")
}

val sourcesJar by tasks.creating(Jar::class) {
    dependsOn("classes")
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

artifacts {
    archives(sourcesJar)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
    repositories {
        maven {
            url = uri("$buildDir/repository")
        }
    }
}

bintray {
    user = bintrayUsername
    key = bintrayApiKey
    setPublications("default")
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = project.name
        name = rootProject.name
        userOrg = "sparetimedevs"
        setLabels("kotlin")
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/sparetimedevs/suspendmongo.git"
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = project.version as? String
            released = Date().toString()
        })
    })
}
