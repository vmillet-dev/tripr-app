dependencies {
    implementation(libs.slf4j)

    testRuntimeOnly(libs.junit.platform.launcher)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.assertJ)
}
