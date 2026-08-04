package com.example.virtual_steer.model

data class NetworkConfig(
    val autoDiscover: Boolean = true,
    val manualIp: String = "192.168.1.100",
    val udpPort: Int = 4444,
    val packetRate: Int = 100, // Hz
    val connectionTimeout: Int = 5000 // ms
)
