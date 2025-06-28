plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("maven-publish")
}

group = "org.acmsl"
version = "latest-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    
    testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test"))
    testImplementation(gradleTestKit())
}

gradlePlugin {
    plugins {
        create("bytehotPlugin") {
            id = "org.acmsl.bytehot"
            implementationClass = "org.acmsl.bytehot.gradle.ByteHotPlugin"
            displayName = "ByteHot Gradle Plugin"
            description = "Seamless live mode activation for Gradle projects with zero-configuration setup"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            pom {
                name.set("ByteHot Gradle Plugin")
                description.set("ByteHot Gradle Plugin - Seamless live mode activation for Gradle projects")
                url.set("https://github.com/rydnr/bytehot")
                
                licenses {
                    license {
                        name.set("GNU General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
                    }
                }
                
                scm {
                    connection.set("scm:git:https://github.com/rydnr/bytehot.git")
                    developerConnection.set("scm:git:git@github.com/rydnr/bytehot.git")
                    url.set("https://github.com/rydnr/bytehot")
                }
            }
        }
    }
}