package com.adriandeleon.template.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.adriandeleon.template.root.DefaultRootComponent
import com.adriandeleon.template.root.RootView
import com.arkivanov.decompose.retainedComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val root = retainedComponent { DefaultRootComponent(it) }

        setContent { RootView(component = root) }
    }
}
