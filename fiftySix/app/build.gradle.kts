plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("de.mannodermaus.android-junit5")
}

android {
    namespace = "com.example.fiftysix"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fiftysix"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    //implementation("http://dl.bintray.com/amulyakhare/maven")
    //implementation("com.amulyakhare:com.amulyakhare.textdrawable:1.0.1")

    implementation("jp.wasabeef:picasso-transformations:2.4.0")

    // Aggregator dependency on JUnit api, engine, and params
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
    // (Optional) If you also have JUnit 4-based tests
    testImplementation("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.9.1")


    // zxing-android-embedded QR code scanning & generation
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.4.1")

    // https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/README.md
    implementation("com.blankj:utilcodex:1.31.1")

    // https://www.geeksforgeeks.org/how-to-retrieve-image-from-firebase-in-realtime-in-android/
    implementation("com.squareup.picasso:picasso:2.71828")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation("com.google.firebase:firebase-analytics")

    //Recycler View
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
    
    // cardView Library not
    implementation("androidx.cardview:cardview:1.0.0")
    //implementation ("io.grpc:grpc-okhttp:1.32.2")

    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-firestore:24.10.3")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.1.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.2")

    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:1.3.0")
    implementation("com.google.firebase:firebase-messaging:20.2.4")
    implementation("com.google.android.play:core:1.10.3")
}