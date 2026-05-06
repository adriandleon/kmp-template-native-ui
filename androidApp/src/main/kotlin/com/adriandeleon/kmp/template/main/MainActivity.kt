package com.adriandeleon.kmp.template.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.adriandeleon.kmp.template.root.RootView
import com.adriandeleon.kmp.template.root.createRootComponent
import com.arkivanov.decompose.retainedComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val root = retainedComponent { createRootComponent(it) }
        splashScreen.setKeepOnScreenCondition { root.isStarting.value }

        setContent { RootView(component = root) }
    }
}
