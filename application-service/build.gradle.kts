import org.springframework.boot.buildpack.platform.build.PullPolicy

plugins {
    java
    jacoco
    checkstyle
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.sonarqube") version "5.0.0.4638"
}

group = "com.griddynamics"
version = "1.0-SNAPSHOT"

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
        property("sonar.projectKey", "GridMarket")
        property("sonar.sources", "src/main")
        property("sonar.tests", "src/test")
        property("sonar.coverage.exclusions", "**/models/*,**/configuration/*")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.griddynamics:test-utils:1.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("com.griddynamics:jackson-jsonapi:1.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestCoverageVerification {

    violationRules {
        rule {
            classDirectories.setFrom(tasks.jacocoTestReport.get().classDirectories)
            limit {
                minimum = 0.7.toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

tasks.sonar {
    dependsOn(tasks.jacocoTestReport)
}

tasks.bootBuildImage {
    pullPolicy = PullPolicy.IF_NOT_PRESENT
}

tasks.jacocoTestReport {
    reports {
        csv.required = false
        xml.required = true
        html.outputLocation = layout.buildDirectory.dir("jacocoHtmlReport")
    }
    dependsOn(tasks.test)
    finalizedBy(tasks.jacocoTestCoverageVerification)
    classDirectories.setFrom(classDirectories.files.map {
        fileTree(it).apply {
            exclude("**/models/*", "**/configuration/*")
        }
    })
}