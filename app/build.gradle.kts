plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
//    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.cyh128.hikarinovel"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cyh128.hikari_novel"
        minSdk = 24
        targetSdk = 34
        versionCode = 241007
        versionName = "3.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

//    splits {
//        abi {
//            isEnable = true
//            isUniversalApk = true
//            reset()
//            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
//        }
//    }

    signingConfigs {
        create("release") {
            storeFile = file("$rootDir/app/src/key.jks")
            storePassword = "123456"
            keyAlias = "key"
            keyPassword = "123456"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

}

dependencies {
    //android相关
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.13.0-alpha06")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.2")

    //数据库
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    //图片查看
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    //rxhttp 网络请求
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.github.liujingxing.rxhttp:rxhttp:3.2.4")
    ksp("com.github.liujingxing.rxhttp:rxhttp-compiler:3.2.4")

    //lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")

    //协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    //html解析
    implementation("org.jsoup:jsoup:1.18.1")

    //加强版recyclerview
    implementation("com.github.youlookwhat:ByRecyclerView:1.3.7")

    //fragment管理
    implementation("com.trendyol:medusa:0.12.1")

    //持久化存储
    implementation("com.tencent:mmkv:1.3.7")

    //图片加载
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //refresh layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //可展开的textview
    implementation("com.github.MZCretin:ExpandableTextView:v1.6.1-x")

    //二级recyclerview
    implementation("com.github.pokercc:ExpandableRecyclerView:0.9.3")

    //崩溃拦截
    implementation("com.github.TutorialsAndroid:crashx:v6.0.19")

    //statusBar与navigationBar透明
    implementation("com.geyifeng.immersionbar:immersionbar:3.2.2")

    //hilt依赖注入
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51")

    //json解析
    implementation("com.google.code.gson:gson:2.11.0")

    //快速切换语言
    implementation("com.github.YarikSOffice:lingver:1.3.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //内存泄漏查看
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")
}