plugins {
	// Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
	id("org.jetbrains.kotlin.jvm") version "1.9.20"

	// Apply the application plugin to add support for building a CLI application in Java.
	application
}

group = "ch.slv.ifinance"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

repositories {
	mavenCentral()
}

val kotlinVersion = "1.9.20"
dependencies {

	implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
	implementation("com.github.ajalt.clikt:clikt-jvm:4.2.1")
	implementation("net.sf.supercsv:super-csv:2.4.0")
	implementation("ch.qos.logback:logback-classic:1.4.11")
}

testing {
	suites {
		// Configure the built-in test suite
		val test by getting(JvmTestSuite::class) {
			// Use Kotlin Test test framework
			useKotlinTest(kotlinVersion)
		}
	}
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

application {
	mainClass.set("ch.slv.ifinance.csvconverter.AppKt")
}

//tasks.test {
//	useJUnitPlatform()
//}
//
//tasks.withType<KotlinCompile> {
//	kotlinOptions {
//		jvmTarget = "21"
//		freeCompilerArgs = listOf("-Xjvm-default=all")
//	}
//}
//
//tasks.jar {
//	manifest {
//		attributes["Main-Class"] = "your.main.Class"
//	}
//}
