@file:Suppress("UnstableApiUsage")

plugins {
	id("org.jetbrains.kotlin.jvm")
	application
	id("io.github.goooler.shadow")
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
	implementation(Kotlin.stdlib)
	implementation("com.github.ajalt.clikt:clikt:_")
	implementation("net.sf.supercsv:super-csv:_")
	implementation("ch.qos.logback:logback-classic:_")
}

testing {
	suites {
		val test by getting(JvmTestSuite::class) {
			useKotlinTest(kotlinVersion)
		}
	}
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

application.applicationName = "akbToIFinance"
application {
	mainClass.set("ch.slv.ifinance.csvconverter.AkbToIFinanceKt")
}
