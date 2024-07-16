import org.springframework.boot.buildpack.platform.build.PullPolicy

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
        property("sonar.projectKey", "GidMarket-AuthService")
        property("sonar.sources", "src/main")
        property("sonar.tests", "src/test")
        property("sonar.coverage.exclusions", "**/models/*,**/configuration/*")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
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

tasks.bootBuildImage {
    pullPolicy = PullPolicy.IF_NOT_PRESENT
}