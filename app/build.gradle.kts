import org.jetbrains.kotlin.gradle.dsl.JvmTarget

//val org.gradle.accessors.dm.LibrariesForLibs.AndroidxLifecycleLibraryAccessors.viewmodel: kotlin.Any



//val org.gradle.accessors.dm.LibrariesForLibs.AndroidxLifecycleLibraryAccessors.viewmodel: kotlin.Any



// Refactored to use Kotlin DSL and Version Catalogs as requested.

plugins {
    // Using aliases as required for the Version Catalog setup
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // The kotlin.compose plugin is deprecated with modern Compose Compiler, can be removed if you add the new one
    // For now, leaving it as it was in your file.
    alias(libs.plugins.kotlin.compose)
}

android {
    // IMPORTANT: Retain the package name that was successfully saved in Auth0
    namespace = "com.example.autentication_app"
    
    // Setting Sdk to 36 to match the previous functional configuration
    compileSdk = 36
    
    defaultConfig {
        // IMPORTANT: Must match the saved Auth0 callback URL host
        applicationId = "com.example.autentication_app" 
        minSdk = 24
        // TargetSdk set to 36 to match compileSdk for stability
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // --- Manifest Placeholders for Auth0 (NEW) ---
        // This is necessary to inject values into the AndroidManifest.xml
        manifestPlaceholders["auth0Domain"] = "dev-k6wz2clanwpqjgyf.us.auth0.com"
        // The scheme is always 'demo' + the application ID, as you configured in Auth0
        manifestPlaceholders["auth0Scheme"] = "demo"
        
        // --- AUTH0 SECRET INJECTION (Remains for ViewModel use) ---
        // Using literal strings for BuildConfig fields to ensure immediate functionality
        buildConfigField("String", "AUTH0_DOMAIN", "\"dev-k6wz2clanwpqjgyf.us.auth0.com\"")
        buildConfigField("String", "AUTH0_CLIENT_ID", "\"QebDSlUnOLAEs6o0r3lpZDff2YARkWSa\"")
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
        // Updated to Java 11 as requested
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    // ✅ **FIX APPLIED HERE**
    kotlin {
        // The 'kotlinOptions' block is no longer needed for this.
        // We now use a 'kotlin' block with 'compilerOptions' inside.
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    buildFeatures {
        // Essential for Jetpack Compose
        compose = true
        // Essential for Auth0 BuildConfig fields
        buildConfig = true
    }
}


dependencies {
    // --- Auth0 Dependencies ---
    implementation(libs.auth0.android)
    // ✅ FIX: Explicitly adding the required java-jwt library.
    // This resolves the Unresolved Reference errors (JWT, DecodedJWT, JWTDecodeException).
    implementation(libs.java.jwt)
    
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // --- Compose & AndroidX Dependencies ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // ✅ FIX: Corrected and de-duplicated lifecycle dependencies. Each on its own line.
    implementation(libs.androidx.lifecycle.viewmodel.compose) // For viewModel() in Compose
    implementation(libs.androidx.lifecycle.livedata.ktx)      // For LiveData support
    implementation("androidx.compose.runtime:runtime-livedata:1.6.0")
    // Compose Bill of Materials (BOM)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // --- Testing Dependencies ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
