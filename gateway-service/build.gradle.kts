plugins {
    java
    checkstyle
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.sonarqube") version "5.0.0.4638"
}

group = "com.griddynamics"
version = "v0.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    sourceCompatibility = JavaVersion.VERSION_21
}

checkstyle {
    toolVersion = "10.14.2"
    isIgnoreFailures = false
    maxWarnings = 0
    configFile = File("../config/checkstyle/checkstyle.xml")
}

sonar {
    properties {
        property("sonar.projectKey", "GidMarket-GatewayService")
        property("sonar.sources", "src/main")
        property("sonar.tests", "src/test")
        property("sonar.coverage.exclusions", "**/configuration/*")
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2023.0.2"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.register("printVersion") {
    group = "documentation"
    description = "Prints current project version"
    doLast {
        println(project.version)
    }
}

tasks.test {
    useJUnitPlatform()
}