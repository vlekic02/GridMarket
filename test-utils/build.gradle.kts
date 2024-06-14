plugins {
    java
    checkstyle
    `maven-publish`
}

group = "com.griddynamics"
version = "1.0"

checkstyle {
    toolVersion = "10.14.2"
    isIgnoreFailures = false
    maxWarnings = 0
    configFile = File("../config/checkstyle/checkstyle.xml")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("org.springframework:spring-web:6.1.9")
    compileOnly("io.micrometer:micrometer-core:1.13.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.griddynamics"
            artifactId = "test-utils"
            version = version

            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}