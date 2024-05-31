plugins {
    java
}

group = "com.griddynamics"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("com.fasterxml.jackson.core:jackson-core:2.17.1")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.17.1")
}

tasks.test {
    useJUnitPlatform()
}