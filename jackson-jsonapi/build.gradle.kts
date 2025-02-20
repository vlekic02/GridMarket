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

configurations {
    testImplementation.get().apply {
        extendsFrom(compileOnly.get())
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.griddynamics"
            artifactId = "jackson-jsonapi"
            version = version

            from(components["java"])
        }
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("com.fasterxml.jackson.core:jackson-core:2.17.1")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    compileOnly("io.swagger.core.v3:swagger-models:2.2.22")
    compileOnly("org.slf4j:slf4j-api:2.0.13")
}

tasks.test {
    useJUnitPlatform()
}

tasks.publishToMavenLocal {
    dependsOn(tasks.check)
}