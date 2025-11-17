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

    implementation("org.ccil.cowan.tagsoup:tagsoup:1.2.1")
    implementation("org.jsoup:jsoup:1.21.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")

}

tasks.withType<Test> {
    useJUnitPlatform()
}
