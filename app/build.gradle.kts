plugins {
    alias(libs.plugins.android.application) // Plugin de aplicación Android (AGP)
    alias(libs.plugins.kotlin.android) // Plugin de Kotlin para Android
    id("kotlin-parcelize")
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android { // Bloque principal de configuración Android
    namespace = "com.miapp.xanostorekotlin" // Paquete base para R y BuildConfig
    compileSdk = 36 // API level de compilación (Android 15)

    defaultConfig { // Configuración por defecto del módulo app
        applicationId = "com.miapp.xanostorekotlin" // ID único del paquete de la app
        minSdk = 24 // Mínimo nivel de API soportado
        targetSdk = 36 // Nivel de API objetivo
        versionCode = 1 // Código de versión para Play Store
        versionName = "1.0" // Nombre de versión mostrado

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // Runner para tests instrumentados
    }

    buildTypes { // Tipos de build (debug/release)
        release { // Configuración para versión release
            isMinifyEnabled = true // No minificar para facilitar depuración
            proguardFiles( // Archivos de reglas Proguard/R8
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures { // Activamos features del módulo
        viewBinding = true // Generación de clases de binding por layout
        dataBinding = true // Generación de clases de binding por layout
        buildConfig = true // Generación de BuildConfig con campos custom
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

}

kotlin {
    jvmToolchain(17)
}

dependencies { // Dependencias del módulo

    implementation("com.google.code.gson:gson:2.10.1")

    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation(libs.androidx.core.ktx) // Extensiones Kotlin para Android core
    implementation(libs.androidx.appcompat) // Compatibilidad de componentes UI
    implementation(libs.material) // Componentes Material Design
    implementation(libs.androidx.recyclerview) // Lista y grids eficientes
    implementation(libs.androidx.constraintlayout) // Layout flexible para vistas
    implementation(libs.androidx.lifecycle.runtime.ktx) // Corrutinas y lifecycle integrados
    implementation(libs.androidx.activity.ktx) // Extensiones para Activities en Kotlin

    // Retrofit / OkHttp / Gson
    implementation(libs.retrofit) // Cliente HTTP de alto nivel
    implementation(libs.converter.gson) // Convertidor JSON usando Gson
    implementation(libs.okhttp) // Cliente HTTP subyacente
    implementation(libs.okhttp.logging) // Interceptor de logging para depuración

    // Corrutinas
    implementation(libs.kotlinx.coroutines.android) // Soporte de corrutinas en Android

    // Imágenes
    implementation(libs.coil)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    testImplementation(libs.junit) // Unit testing con JUnit4
    androidTestImplementation(libs.androidx.junit) // Testing instrumentado (JUnit ext)
    androidTestImplementation(libs.androidx.espresso.core) // Testing de UI con Espresso
}
