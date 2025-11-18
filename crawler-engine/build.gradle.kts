plugins {
    id("java-library")
}

group = "com.crawler"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":crawler-model"))

    implementation("org.jsoup:jsoup:1.21.2")
    implementation("org.slf4j:slf4j-api:2.0.1")
    implementation("ch.qos.logback:logback-classic:1.5.21")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")

}

tasks.withType<Test> {
    useJUnitPlatform()
}
