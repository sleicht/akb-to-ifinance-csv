@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.9.20"
	application
	id("io.github.goooler.shadow") version "8.1.2"
}

group = "ch.slv.ifinance"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

repositories {
	mavenCentral()
}

val kotlinVersion = (plugins.getPlugin("org.jetbrains.kotlin.jvm") as KotlinBasePlugin).pluginVersion
dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
	implementation("com.github.ajalt.clikt:clikt:4.2.1")
	implementation("net.sf.supercsv:super-csv:2.4.0")
	implementation("ch.qos.logback:logback-classic:1.4.11")
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
