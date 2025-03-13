plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.cyh128.hikari_novel"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cyh128.hikari_novel"
        minSdk = 24
        targetSdk = 34 //暂时不要升到35，不然之前的edgeToEdge效果会消失
        versionCode = 250315
        versionName = "3.9.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    splits {
        abi {
            isEnable = true
            isUniversalApk = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    signingConfigs {
        create("signing") {
            storeFile = file("$rootDir/app/src/key.jks")
            storePassword = "123456"
            keyAlias = "key"
            keyPassword = "123456"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("signing")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("signing")
            applicationIdSuffix = ".DEBUG"
            versionNameSuffix = " DEBUG"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    //android相关
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.13.0-alpha11")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.8")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.8")

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
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    //协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    //html解析
    implementation("org.jsoup:jsoup:1.18.1")

    //加强版recyclerview
    implementation("com.github.youlookwhat:ByRecyclerView:1.3.7")

    //fragment管理
    implementation("com.trendyol:medusa:0.12.1")

    //持久化存储
    //请勿修改此版本，否则app会不支持32位设备
    //noinspection GradleDependency
    implementation("com.tencent:mmkv:1.3.11")

    //图片加载
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.16.0")

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
    implementation("com.google.dagger:hilt-android:2.55")
    ksp("com.google.dagger:hilt-android-compiler:2.55")

    //json解析
    implementation("com.google.code.gson:gson:2.11.0")

    //事件总线
    implementation("com.github.liangjingkanji:Channel:1.1.5")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //内存泄漏查看
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")
}