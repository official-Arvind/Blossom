plugins {
    id("com.android.application")
}
dependencies {
    implementation("com.github.MetrolistGroup:MetrolistExtractor:f0a00f5") {
        exclude(group = "com.google.protobuf")
    }
}
