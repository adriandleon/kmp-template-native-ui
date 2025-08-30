package com.adriandeleon.template

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
