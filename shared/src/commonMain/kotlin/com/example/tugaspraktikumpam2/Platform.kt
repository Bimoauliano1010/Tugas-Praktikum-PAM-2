package com.example.tugaspraktikumpam2

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform