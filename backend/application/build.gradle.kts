dependencies {
    implementation(project(":domain"))

    implementation(libs.slf4j)
    
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.springboot.starter.test)
}
