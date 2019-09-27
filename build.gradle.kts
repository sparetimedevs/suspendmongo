import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Date

group = "com.sparetimedevs"
version = "0.0.1-EXPERIMENTAL-t1fr2v"

plugins {
	`build-scan`
	`maven-publish`
	id("org.jetbrains.kotlin.jvm") version "1.3.50"
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

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "1.8"
}

dependencies {
	val mongoDbGroup = "org.mongodb"
	val nettyGroup = "io.netty"
	val kotlinGroup = "org.jetbrains.kotlin"
	val kotlinxGroup = "org.jetbrains.kotlinx"
	val resilience4jGroup = "io.github.resilience4j"
	val rxjava2Group = "io.reactivex.rxjava2"
	val kotlinTestGroup = "io.kotlintest"
	val mockkGroup = "io.mockk"
	val testcontainersGroup = "org.testcontainers"
	
	val mongoDbBsonArtifact = "bson"
	val mongoDbCoreArtifact = "mongodb-driver-core"
	val nettyAllArtifact = "netty-all"
	val kotlinStdLibJdk8Artifact = "kotlin-stdlib-jdk8"
	val kotlinxCoroutinesCoreArtifact = "kotlinx-coroutines-core"
	val kotlinxCoroutinesReactiveArtifact = "kotlinx-coroutines-reactive"
	val mongoDbDriverReactiveStreamsArtifact = "mongodb-driver-reactivestreams"
	val resilience4jRxJava2Artifact = "resilience4j-rxjava2"
	val resilience4jRetryArtifact = "resilience4j-retry"
	val resilience4jBulkheadArtifact = "resilience4j-bulkhead"
	val rxjavaArtifact = "rxjava"
	val kotlinTestRunnerJUnit5Artifact = "kotlintest-runner-junit5"
	val mockkArtifact = "mockk"
	val testcontainersToxiproxyArtifact = "toxiproxy"
	val mongoDbDriverSyncArtifact = "mongodb-driver-sync"
	
	val mongoDbVersion: String by project
	val nettyAllVersion: String by project
	val kotlinStdLibJdk8Version: String by project
	val kotlinxVersion: String by project
	val mongodbDriverReactivestreamsVersion: String by project
	val resilienceVersion: String by project
	val rxjava2Version: String by project
	val kotlinTestVersion: String by project
	val mockkVersion: String by project
	val testcontainersVersion: String by project
	
	api(mongoDbGroup, mongoDbBsonArtifact, mongoDbVersion)
	api(mongoDbGroup, mongoDbCoreArtifact, mongoDbVersion)
	api(nettyGroup, nettyAllArtifact, nettyAllVersion)
	
	implementation(kotlinGroup, kotlinStdLibJdk8Artifact, kotlinStdLibJdk8Version)
	implementation(kotlinxGroup, kotlinxCoroutinesCoreArtifact, kotlinxVersion)
	implementation(kotlinxGroup, kotlinxCoroutinesReactiveArtifact, kotlinxVersion)
	implementation(mongoDbGroup, mongoDbDriverReactiveStreamsArtifact, mongodbDriverReactivestreamsVersion)
	implementation(resilience4jGroup, resilience4jRxJava2Artifact, resilienceVersion)
	implementation(resilience4jGroup, resilience4jRetryArtifact, resilienceVersion)
	implementation(resilience4jGroup, resilience4jBulkheadArtifact, resilienceVersion)
	implementation(rxjava2Group, rxjavaArtifact,rxjava2Version)
	
	testImplementation(kotlinTestGroup, kotlinTestRunnerJUnit5Artifact, kotlinTestVersion)
	testImplementation(mockkGroup, mockkArtifact, mockkVersion)
	testImplementation(testcontainersGroup, testcontainersToxiproxyArtifact, testcontainersVersion)
	testImplementation(mongoDbGroup, mongoDbDriverSyncArtifact, mongoDbVersion)
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
	val bintrayUsername: String? by project
	val bintrayApiKey: String? by project
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
