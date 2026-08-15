package com.example.virtual_steer.network

import android.util.Log
import com.example.virtual_steer.model.DiscoveredServer
import com.example.virtual_steer.model.DiscoveryResponse
import com.example.virtual_steer.model.NetworkConfig
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

class DiscoveryClient(
    private val scope: CoroutineScope,
    private val onServerFound: (DiscoveredServer) -> Unit
) {
    private val TAG = "DiscoveryClient"
    private val DISCOVERY_PORT = 4445
    private val DISCOVERY_MSG = "DISCOVER_VIRTUAL_STEER"
    
    private var job: Job? = null
    private var config: NetworkConfig = NetworkConfig()
    
    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }

    fun updateConfig(newConfig: NetworkConfig) {
        val wasActive = job != null
        config = newConfig
        if (wasActive && !config.autoDiscover) {
            stop()
        } else if (!wasActive && config.autoDiscover) {
            start()
        }
    }

    fun start() {
        if (job != null) return

        job = scope.launch(Dispatchers.IO) {
            var socket: DatagramSocket? = null
            try {
                socket = DatagramSocket(null).apply {
                    reuseAddress = true
                    broadcast = true
                    bind(InetSocketAddress(DISCOVERY_PORT))
                }
                
                // Start a sub-job for periodic broadcasting to all active network interface broadcast addresses
                val broadcaster = launch {
                    val sendData = DISCOVERY_MSG.toByteArray()
                    while (isActive) {
                        try {
                            val broadcastAddresses = mutableListOf<InetAddress>()
                            // Always fallback to global broadcast
                            broadcastAddresses.add(InetAddress.getByName("255.255.255.255"))

                            try {
                                val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
                                if (interfaces != null) {
                                    for (networkInterface in java.util.Collections.list(interfaces)) {
                                        if (networkInterface.isLoopback || !networkInterface.isUp) continue
                                        for (interfaceAddress in networkInterface.interfaceAddresses) {
                                            val broadcast = interfaceAddress.broadcast
                                            if (broadcast != null) {
                                                broadcastAddresses.add(broadcast)
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to retrieve local interface broadcasts", e)
                            }

                            // De-duplicate addresses
                            val uniqueAddresses = broadcastAddresses.distinct()

                            for (broadcastAddr in uniqueAddresses) {
                                try {
                                    Log.d(TAG, "Broadcasting discovery request to $broadcastAddr...")
                                    val sendPacket = DatagramPacket(sendData, sendData.size, broadcastAddr, DISCOVERY_PORT)
                                    socket.send(sendPacket)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Broadcast failed to $broadcastAddr", e)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Discovery broadcast round failed", e)
                        }
                        delay(3000) // Every 3 seconds
                    }
                }

                val buffer = ByteArray(2048)
                val packet = DatagramPacket(buffer, buffer.size)

                Log.d(TAG, "Listening for discovery replies on port $DISCOVERY_PORT")

                while (isActive) {
                    socket.receive(packet)
                    val message = String(packet.data, 0, packet.length).trim()
                    val senderIp = packet.address.hostAddress ?: continue
                    
                    // Ignore our own broadcast message
                    if (message == DISCOVERY_MSG) continue

                    Log.d(TAG, "Received discovery reply from $senderIp: $message")

                    try {
                        val connType = getInterfaceTypeForIp(senderIp)
                        if (message.startsWith("{")) {
                            // Try JSON parsing
                            val response = json.decodeFromString<DiscoveryResponse>(message)
                            onServerFound(DiscoveredServer(senderIp, response.port, response.hostname, connectionType = connType))
                        } else if (message.startsWith("VIRTUAL_STEER_SERVER:")) {
                            // Try legacy string parsing
                            val parts = message.split(":")
                            val serverPort = parts.getOrNull(1)?.toIntOrNull() ?: 4444
                            val serverName = parts.getOrNull(2) ?: "Unknown PC"
                            onServerFound(DiscoveredServer(senderIp, serverPort, serverName, connectionType = connType))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse discovery message", e)
                    }
                }
            } catch (e: Exception) {
                if (isActive) {
                    Log.e(TAG, "Discovery error", e)
                }
            } finally {
                socket?.close()
            }
        }
    }

    private fun getInterfaceTypeForIp(ipAddress: String): String {
        try {
            val targetIp = InetAddress.getByName(ipAddress)
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            if (interfaces != null) {
                for (netInterface in java.util.Collections.list(interfaces)) {
                    if (netInterface.isLoopback || !netInterface.isUp) continue
                    for (interfaceAddress in netInterface.interfaceAddresses) {
                        val localIp = interfaceAddress.address
                        val prefixLength = interfaceAddress.networkPrefixLength
                        if (isInSameSubnet(localIp, targetIp, prefixLength)) {
                            val name = netInterface.name.lowercase()
                            return if (name.contains("rndis") || name.contains("usb")) {
                                "USB"
                            } else {
                                "Wi-Fi"
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to determine interface type", e)
        }
        // Fallback based on typical USB tethering subnets if subnet matching failed
        if (ipAddress.startsWith("192.168.42.")) {
            return "USB"
        }
        return "Wi-Fi"
    }

    private fun isInSameSubnet(ip1: InetAddress, ip2: InetAddress, prefixLength: Short): Boolean {
        val bytes1 = ip1.address
        val bytes2 = ip2.address
        if (bytes1.size != bytes2.size) return false
        
        val bits = prefixLength.toInt()
        val bytesToCheck = bits / 8
        val remainingBits = bits % 8
        
        for (i in 0 until bytesToCheck) {
            if (bytes1[i] != bytes2[i]) return false
        }
        
        if (remainingBits > 0) {
            val mask = (0xFF00 shr remainingBits).toByte()
            if ((bytes1[bytesToCheck].toInt() and mask.toInt()) != (bytes2[bytesToCheck].toInt() and mask.toInt())) {
                return false
            }
        }
        
        return true
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
