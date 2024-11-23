plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.ilya"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.telegram:telegrambots-spring-boot-starter:6.9.7.0")
	implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.shadowJar {
	archiveBaseName.set("bot") // Имя JAR-файла
	archiveClassifier.set("")  // Оставляем пустым для простого имени файла
	archiveVersion.set("1.0")  // Версия
	manifest {
		attributes["Main-Class"] = "com.ilya.filmCodesBot.FilmCodesBotApplicationKt" // Укажите ваш главный класс
	}
}

tasks.jar {
	manifest {
		attributes["Main-Class"] = "com.ilya.filmCodesBot.FilmCodesBotApplicationKt"
	}
	from(sourceSets.main.get().output)
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Test> {
	useJUnitPlatform()
}
