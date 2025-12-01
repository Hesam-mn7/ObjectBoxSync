// <project-root>/build.gradle.kts

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // از نسخه‌کاتالوگ میاد (libs.versions.toml)
        classpath(libs.objectbox.gradle.plugin)
    }
}

// اینجا فقط پلاگین‌ها رو برای ساب‌پروجکت‌ها define می‌کنیم
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
